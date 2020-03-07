/*
** 2011 April 5
**
** The author disclaims copyright to this source code.  In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
*/

package info.ata4.bsplib;

import info.ata4.bsplib.app.SourceApp;
import info.ata4.bsplib.app.SourceAppDB;
import info.ata4.bsplib.app.SourceAppID;
import info.ata4.bsplib.entity.Entity;
import info.ata4.bsplib.lump.GameLump;
import info.ata4.bsplib.lump.Lump;
import info.ata4.bsplib.lump.LumpType;
import info.ata4.bsplib.lump.contentreader.*;
import info.ata4.bsplib.struct.*;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static info.ata4.bsplib.app.SourceAppID.*;

/**
 * All-purpose BSP file and lump reader.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BspFileReader {

    private static final Logger L = LogUtils.getLogger();

    // BSP headers and data
    private final BspFile bspFile;
    private final BspData bspData;
    private int appID;

    public BspFileReader(BspFile bspFile, BspData bspData) throws IOException {
        this.bspFile = bspFile;
        this.bspData = bspData;
        this.appID = bspFile.getSourceApp().getAppID();

        if (bspFile.getFile() == null) {
            // "Gah! Hear me, man? Gah!"
            throw new BspException("BSP file is unloaded");
        }

        // uncompress all lumps first
        if (bspFile.isCompressed()) {
            bspFile.uncompress();
        }
    }

    public BspFileReader(BspFile bspFile) throws IOException {
        this(bspFile, new BspData());
    }

    /**
     * Loads all supported lumps
     */
    public void loadAll() {
        loadEntities();
        loadVertices();
        loadEdges();
        loadFaces();
        loadOriginalFaces();
        loadModels();
        loadSurfaceEdges();
        loadOccluders();
        loadTexInfo();
        loadTexData();
        loadStaticProps();
        loadCubemaps();
        loadPlanes();
        loadBrushes();
        loadBrushSides();
        loadAreaportals();
        loadClipPortalVertices();
        loadDispInfos();
        loadDispVertices();
        loadDispTriangleTags();
        loadDispMultiBlend();
        loadNodes();
        loadLeaves();
        loadLeafFaces();
        loadLeafBrushes();
        loadOverlays();
        loadFlags();
    }

    private <E extends DStruct> List<E> readPacketLump(LumpType lumpType, Supplier<E> dStructSupplier) {
        DStructPacketsContentReader<E> contentReader = DStructPacketsContentReader.forAllBytes(dStructSupplier);
        return readLump(lumpType, contentReader);
    }

    private List<Integer> readIntegerPacketLump(LumpType lumpType) {
        return readLump(lumpType, IntegerPacketsContentReader.forAllBytes());
    }

    private List<Integer> readUShortPacketLump(LumpType lumpType) {
        return readLump(lumpType, UShortPacketsContentReader.forAllBytes());
    }

    private <T> T readLump(LumpType lumpType, LumpContentReader<T> contentReader) {
        try {
            return bspFile.readLumpContent(lumpType, contentReader);
        } catch (LumpContentReadException e) {
            L.log(Level.SEVERE, "Error reading lump " + lumpType, e);

            // We don't return null but rather ask the LumpContentLoader for a 'null' value
            // We do this in hopes, that the software can continue execution without exceptions
            // Eg: For a LumpContentLoader that returns a list of elements, the 'null' value could be an empty list
            return contentReader.nullData();
        }
    }

    private <T> T readGameLump(String lumpSid, LumpContentReader<T> contentReader) {
        try {
            return bspFile.readGameLumpContent(lumpSid, contentReader);
        } catch (LumpContentReadException e) {
            L.log(Level.SEVERE, "Error reading game lump " + lumpSid, e);

            // We don't return null but rather ask the LumpContentLoader for a 'null' value
            // We do this in hopes, that the software can continue execution without exceptions
            // Eg: For a LumpContentLoader that returns a list of elements, the 'null' value could be an empty list
            return contentReader.nullData();
        }
    }

    public void loadPlanes() {
        if (bspData.planes != null) {
            return;
        }

        bspData.planes = readPacketLump(LumpType.LUMP_PLANES, DPlane::new);
    }

    public void loadBrushes() {
        if (bspData.brushes != null) {
            return;
        }

        bspData.brushes = readPacketLump(LumpType.LUMP_BRUSHES, DBrush::new);
    }

    public void loadBrushSides() {
        if (bspData.brushSides != null) {
            return;
        }

        Supplier<? extends DBrushSide> dStructSupplier;

        if (appID == VINDICTUS) {
            dStructSupplier = DBrushSideVin::new;
        } else if (bspFile.getVersion() >= 21 && appID != LEFT_4_DEAD_2) {
            // newer BSP files have a slightly different struct that is still reported
            // as version 0
            dStructSupplier = DBrushSideV2::new;
        } else {
            dStructSupplier = DBrushSide::new;
        }

        bspData.brushSides = readPacketLump(LumpType.LUMP_BRUSHSIDES, dStructSupplier);
    }

    public void loadVertices() {
        if (bspData.verts != null) {
            return;
        }

        bspData.verts = readPacketLump(LumpType.LUMP_VERTEXES, DVertex::new);
    }

    public void loadClipPortalVertices() {
        if (bspData.clipPortalVerts != null) {
            return;
        }

        bspData.clipPortalVerts = readPacketLump(LumpType.LUMP_CLIPPORTALVERTS, DVertex::new);
    }

    public void loadEdges() {
        if (bspData.edges != null) {
            return;
        }

        Supplier<? extends DEdge> dStructSupplier;

        if (appID == VINDICTUS) {
            dStructSupplier = DEdgeVin::new;
        } else {
            dStructSupplier = DEdge::new;
        }

        bspData.edges = readPacketLump(LumpType.LUMP_EDGES, dStructSupplier);
    }

    public void loadFaces() {
        if (bspData.faces != null) {
            return;
        }

        int faceVersion = bspFile.getLump(LumpType.LUMP_FACES).getVersion();
        Supplier<? extends DFace> dStructSupplier = getDFaceSupplier(faceVersion);

        bspData.faces = readPacketLump(LumpType.LUMP_FACES, dStructSupplier);
        bspData.hdrFaces = readPacketLump(LumpType.LUMP_FACES_HDR, dStructSupplier);
    }

    public void loadOriginalFaces() {
        if (bspData.origFaces != null) {
            return;
        }

        int faceVersion = bspFile.getLump(LumpType.LUMP_ORIGINALFACES).getVersion();
        Supplier<? extends DFace> dStructSupplier = getDFaceSupplier(faceVersion);

        bspData.origFaces = readPacketLump(LumpType.LUMP_ORIGINALFACES, dStructSupplier);
    }

    private Supplier<? extends DFace> getDFaceSupplier(int faceVersion) {
        switch (appID) {
            case VAMPIRE_BLOODLINES:
                return DFaceVTMB::new;

            case VINDICTUS:
                if (faceVersion == 2) {
                    return DFaceVinV2::new;
                } else {
                    return DFaceVinV1::new;
                }

            default:
                switch (bspFile.getVersion()) {
                    case 17:
                        return DFaceBSP17::new;

                    case 18:
                        return DFaceBSP18::new;

                    default:
                        return DFace::new;
                }
        }
    }

    public void loadModels() {
        if (bspData.models != null) {
            return;
        }

        Supplier<? extends DModel> dStructSupplier;

        if (appID == DARK_MESSIAH) {
            dStructSupplier = DModelDM::new;
        } else {
            dStructSupplier = DModel::new;
        }

        bspData.models = readPacketLump(LumpType.LUMP_MODELS, dStructSupplier);
    }

    public void loadSurfaceEdges() {
        if (bspData.surfEdges != null) {
            return;
        }

        bspData.surfEdges = readIntegerPacketLump(LumpType.LUMP_SURFEDGES);
    }

    public void loadStaticProps() {
        if (bspData.staticProps != null && bspData.staticPropName != null) {
            return;
        }

        L.fine("Loading static props");

        GameLump sprpLump = bspFile.getGameLump("sprp");

        if (sprpLump == null) {
            // static prop lump not available
            bspData.staticProps = new ArrayList<>();
            return;
        }

        int sprpver = sprpLump.getVersion();

        StaticPropLumpContentReader.StaticPropData staticPropData =
                readGameLump("sprp", new StaticPropLumpContentReader(sprpver, appID));

        bspData.staticProps = staticPropData.staticProps;
        bspData.staticPropName = staticPropData.staticPropDict;
        bspData.staticPropLeaf = staticPropData.staticPropLeaf;
    }

    public void loadCubemaps() {
        if (bspData.cubemaps != null) {
            return;
        }

        bspData.cubemaps = readPacketLump(LumpType.LUMP_CUBEMAPS, DCubemapSample::new);
    }

    public void loadDispInfos() {
        if (bspData.dispinfos != null) {
            return;
        }

        Supplier<? extends DDispInfo> dStructSupplier = DDispInfo::new;
        int bspv = bspFile.getVersion();

        switch (appID) {
            case VINDICTUS:
                dStructSupplier = DDispInfoVin::new;
                break;

            case HALF_LIFE_2:
                if (bspv == 17) {
                    dStructSupplier = DDispInfoBSP17::new;
                }
                break;

            case DOTA_2_BETA:
                if (bspv == 22) {
                    dStructSupplier = DDispInfoBSP22::new;
                } else if (bspv >= 23) {
                    dStructSupplier = DDispInfoBSP23::new;
                }
                break;
        }

        bspData.dispinfos = readPacketLump(LumpType.LUMP_DISPINFO, dStructSupplier);
    }

    public void loadDispVertices() {
        if (bspData.dispverts != null) {
            return;
        }

        bspData.dispverts = readPacketLump(LumpType.LUMP_DISP_VERTS, DDispVert::new);
    }

    public void loadDispTriangleTags() {
        if (bspData.disptris != null) {
            return;
        }

        bspData.disptris = readPacketLump(LumpType.LUMP_DISP_TRIS, DDispTri::new);
    }

    public void loadDispMultiBlend() {
        if (bspData.dispmultiblend != null) {
            return;
        }

        bspData.dispmultiblend = readPacketLump(LumpType.LUMP_DISP_MULTIBLEND, DDispMultiBlend::new);
    }

    public void loadTexInfo() {
        if (bspData.texinfos != null) {
            return;
        }

        Supplier<? extends DTexInfo> dStructSupplier;

        if (appID == DARK_MESSIAH) {
            dStructSupplier = DTexInfoDM::new;
        } else {
            dStructSupplier = DTexInfo::new;
        }

        bspData.texinfos = readPacketLump(LumpType.LUMP_TEXINFO, dStructSupplier);
    }

    public void loadTexData() {
        if (bspData.texdatas != null) {
            return;
        }

        bspData.texdatas = readPacketLump(LumpType.LUMP_TEXDATA, DTexData::new);
        loadTexDataStrings();  // load associated texdata strings
    }

    private void loadTexDataStrings() {

        List<Integer> stringTableData = readIntegerPacketLump(LumpType.LUMP_TEXDATA_STRING_TABLE);
        byte[] stringData = readLump(LumpType.LUMP_TEXDATA_STRING_DATA, new BytesContentReader());

        bspData.texnames = stringTableData.stream()
                .map(offset -> {
                    int offsetNull = offset;
                    while (offsetNull < stringData.length && stringData[offsetNull] != 0) {
                        offsetNull++;
                    }
                    return new String(stringData, offset, offsetNull - offset);
                })
                .collect(Collectors.toList());

        L.log(Level.FINE, "Loading {0}", LumpType.LUMP_TEXDATA_STRING_DATA);
    }

    public void loadEntities() {
        if (bspData.entities != null) {
            return;
        }

        bspData.entities = readLump(LumpType.LUMP_ENTITIES, new EntityLumpContentReader(bspFile.getVersion() == 17));

        Set<String> entityClasses = bspData.entities.stream()
                .map(Entity::getClassName)
                .collect(Collectors.toSet());

        // detect appID with heuristics to handle special BSP formats if it's
        // still unknown or undefined at this point
        if (appID == UNKNOWN) {
            SourceAppDB appDB = SourceAppDB.getInstance();
            SourceApp app = appDB.find(bspFile.getName(), bspFile.getVersion(), entityClasses);
            bspFile.setSourceApp(app);
            appID = app.getAppID();
        }
    }

    public void loadNodes() {
        if (bspData.nodes != null) {
            return;
        }

        Supplier<? extends DNode> dStructSupplier;

        if (appID == VINDICTUS) {
            // use special struct for Vindictus
            dStructSupplier = DNodeVin::new;
        } else {
            dStructSupplier = DNode::new;
        }

        bspData.nodes = readPacketLump(LumpType.LUMP_NODES, dStructSupplier);
    }

    public void loadLeaves() {
        if (bspData.leaves != null) {
            return;
        }

        Supplier<? extends DLeaf> dStructSupplier;

        if (appID == VINDICTUS) {
            // use special struct for Vindictus
            dStructSupplier = DLeafVin::new;
        } else if (bspFile.getLump(LumpType.LUMP_LEAFS).getVersion() == 0 && bspFile.getVersion() == 19) {
            // read AmbientLighting, it was used in initial Half-Life 2 maps 
            // only and doesn't exist in newer or older versions
            dStructSupplier = DLeafV0::new;
        } else {
            dStructSupplier = DLeafV1::new;
        }

        bspData.leaves = readPacketLump(LumpType.LUMP_LEAFS, dStructSupplier);
    }

    public void loadLeafFaces() {
        if (bspData.leafFaces != null) {
            return;
        }

        if (appID == VINDICTUS) {
            bspData.leafFaces = readIntegerPacketLump(LumpType.LUMP_LEAFFACES);
        } else {
            bspData.leafFaces = readUShortPacketLump(LumpType.LUMP_LEAFFACES);
        }
    }

    public void loadLeafBrushes() {
        if (bspData.leafBrushes != null) {
            return;
        }

        if (appID == VINDICTUS) {
            bspData.leafBrushes = readIntegerPacketLump(LumpType.LUMP_LEAFBRUSHES);
        } else {
            bspData.leafBrushes = readUShortPacketLump(LumpType.LUMP_LEAFBRUSHES);
        }
    }

    public void loadOverlays() {
        if (bspData.overlays != null) {
            return;
        }

        Supplier<? extends DOverlay> dStructSupplier;

        if (appID == VINDICTUS) {
            dStructSupplier = DOverlayVin::new;
        } else if (appID == DOTA_2_BETA) {
            dStructSupplier = DOverlayDota2::new;
        } else {
            dStructSupplier = DOverlay::new;
        }

        bspData.overlays = readPacketLump(LumpType.LUMP_OVERLAYS, dStructSupplier);

        // read fade distances
        if (bspData.overlayFades == null) {
            bspData.overlayFades = readPacketLump(LumpType.LUMP_OVERLAY_FADES, DOverlayFade::new);
        }

        // read CPU/GPU levels
        if (bspData.overlaySysLevels == null) {
            bspData.overlaySysLevels = readPacketLump(LumpType.LUMP_OVERLAY_SYSTEM_LEVELS, DOverlaySystemLevel::new);
        }
    }

    public void loadAreaportals() {
        if (bspData.areaportals != null) {
            return;
        }

        Supplier<? extends DAreaportal> dStructSupplier;

        if (appID == VINDICTUS) {
            dStructSupplier = DAreaportalVin::new;
        } else {
            dStructSupplier = DAreaportal::new;
        }

        bspData.areaportals = readPacketLump(LumpType.LUMP_AREAPORTALS, dStructSupplier);
    }

    public void loadOccluders() {
        if (bspData.occluderDatas != null) {
            return;
        }

        Lump lump = bspFile.getLump(LumpType.LUMP_OCCLUSION);
        int lumpVersion = lump.getVersion();

        // Contagion maps report lump version 0, but they're actually
        // using 1
        if (bspFile.getSourceApp().getAppID() == SourceAppID.CONTAGION) {
            lumpVersion = 1;
        }

        Supplier<? extends DOccluderData> dOccluderDataSupplier;
        if (lumpVersion > 0) {
            dOccluderDataSupplier = DOccluderDataV1::new;
        } else {
            dOccluderDataSupplier = DOccluderData::new;
        }

        OcclusionLumpContentReader.OcclusionData<? extends DOccluderData> occlusionData =
                readLump(LumpType.LUMP_OCCLUSION, new OcclusionLumpContentReader<>(dOccluderDataSupplier));

        bspData.occluderDatas = occlusionData.dOccluderData;
        bspData.occluderPolyDatas = occlusionData.dOccluderPolyData;
        bspData.occluderVerts = occlusionData.vertexIndices;
    }

    public void loadFlags() {
        if (bspData.mapFlags != null) {
            return;
        }

        bspData.mapFlags = readLump(LumpType.LUMP_MAP_FLAGS, new FlagsLumpContentReader());
    }

    public void loadPrimitives() {
        if (bspData.prims != null) {
            return;
        }

        bspData.prims = readPacketLump(LumpType.LUMP_PRIMITIVES, DPrimitive::new);
    }

    public void loadPrimIndices() {
        if (bspData.primIndices != null) {
            return;
        }

        bspData.primIndices = readUShortPacketLump(LumpType.LUMP_PRIMINDICES);
    }

    public void loadPrimVerts() {
        if (bspData.primVerts != null) {
            return;
        }

        bspData.primVerts = readPacketLump(LumpType.LUMP_PRIMVERTS, DVertex::new);
    }

    public BspFile getBspFile() {
        return bspFile;
    }

    public BspData getData() {
        return bspData;
    }
}
