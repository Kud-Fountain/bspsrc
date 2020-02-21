package info.ata4.bsplib.lump.contentreader;

import info.ata4.bsplib.struct.*;
import info.ata4.bsplib.vector.Vector3f;
import info.ata4.io.DataReader;
import info.ata4.io.DataReaders;
import info.ata4.log.LogUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static info.ata4.bsplib.app.SourceAppID.*;
import static info.ata4.io.Seekable.Origin.CURRENT;

public class StaticPropLumpContentReader extends AbstractContentReader<StaticPropLumpContentReader.StaticPropData> {

    private static final Logger L = LogUtils.getLogger();

    private final int sprpVersion;
    private final int appId;

    public StaticPropLumpContentReader(int sprpVersion, int appId) {
        super(new StaticPropData());

        this.sprpVersion = sprpVersion;
        this.appId = appId;
    }

    @Override
    public StaticPropData read(ByteBuffer buffer) throws IOException {

        DataReader in = DataReaders.forByteBuffer(buffer);

        List<String> staticPropDict = readDict(in);

        // model path strings in Zeno Clash
        if (appId == ZENO_CLASH) {
            int psextra = in.readInt();
            in.seek(psextra * 128, CURRENT);
        }

        List<Integer> staticPropLeaf = readLeafs(in);

        Map<Integer, Vector3f> scaling;
        if (appId == VINDICTUS && sprpVersion > 5) {
            scaling = readVindictusScaling(in);
        } else {
            scaling = Collections.emptyMap();
        }

        List<? extends DStaticProp> staticProps = readStaticProps(in);

        for (int i = 0; i < staticProps.size(); i++) {
            DStaticProp sp = staticProps.get(i);
            if (scaling.containsKey(i) && sp instanceof DStaticPropVinScaling) {
                ((DStaticPropVinScaling) sp).setScaling(scaling.get(i));
            }
        }

        return new StaticPropData(staticPropDict, staticPropLeaf, staticProps);
    }

    private Map<Integer, Vector3f> readVindictusScaling(DataReader in) throws IOException {
        Map<Integer, Vector3f> scaling = new HashMap<>();

        int scalingCount = in.readInt();
        for (int i = 0; i < scalingCount; i++) {
            scaling.put(in.readInt(), Vector3f.read(in));
        }

        return scaling;
    }

    private List<String> readDict(DataReader in) throws IOException {
        final int psnames = in.readInt();

        List<String> staticPropNames = new ArrayList<>(psnames);

        for (int i = 0; i < psnames; i++) {
            staticPropNames.add(in.readStringFixed(128));
        }

        return staticPropNames;
    }

    private List<Integer> readLeafs(DataReader in) throws IOException {
        final int propleaves = in.readInt();

        List<Integer> staticPropLeaf = new ArrayList<>(propleaves);

        for (int i = 0; i < propleaves; i++) {
            staticPropLeaf.add(in.readUnsignedShort());
        }

        return staticPropLeaf;
    }

    private List<? extends DStaticProp> readStaticProps(DataReader in) throws IOException {
        final int propStaticCount = in.readInt();
        
        // don't try to read static props if there are none
        if (propStaticCount == 0) {
            return Collections.emptyList();
        }
        
        // calculate static prop struct size
        final int propStaticSize = (int) in.remaining() / propStaticCount;

        Class<? extends DStaticProp> structClass = null;

        // special cases where derivative lump structures are used
        switch (appId) {
            case THE_SHIP:
                if (propStaticSize == 188) {
                    structClass = DStaticPropV5Ship.class;
                }
                break;

            case BLOODY_GOOD_TIME:
                if (propStaticSize == 192) {
                    structClass = DStaticPropV6BGT.class;
                }
                break;

            case ZENO_CLASH:
                if (propStaticSize == 68) {
                    structClass = DStaticPropV7ZC.class;
                }
                break;

            case DARK_MESSIAH:
                if (propStaticSize == 136) {
                    structClass = DStaticPropV6DM.class;
                }
                break;

            case DEAR_ESTHER:
                if (propStaticSize == 76) {
                    structClass = DStaticPropV9DE.class;
                }
                break;

            case VINDICTUS:
                // newer maps report v6 even though their structure is identical to DStaticPropV5, probably because
                // they additional have scaling array saved before the static prop array
                // Consequently, their v7 seems to be a standard DStaticPropV6 with an additional scaling array
                if (sprpVersion == 6 && propStaticSize == 60) {
                    structClass = DStaticPropV6VIN.class;
                } else if (sprpVersion == 7 && propStaticSize == 64) {
                    structClass = DStaticPropV7VIN.class;
                }
                break;

            case LEFT_4_DEAD:
                // old L4D maps use v7 that is incompatible to the newer
                // Source 2013 v7
                if (sprpVersion == 7 && propStaticSize == 68) {
                    structClass = DStaticPropV7L4D.class;
                }
                break;

            case TEAM_FORTRESS_2:
                // there's been a short period where TF2 used v7, which later
                // became v10 in all Source 2013 game
                if (sprpVersion == 7 && propStaticSize == 72) {
                    structClass = DStaticPropV10.class;
                }
                break;

            case COUNTER_STRIKE_GO:
                //  (custom v10 for CS:GO, not compatible with Source 2013 v10)  CS:GO now uses v11  since the addition of uniform prop scaling
                if (sprpVersion == 10) {
                    structClass = DStaticPropV10CSGO.class;
                } else if (sprpVersion == 11) {
                    structClass = DStaticPropV11CSGO.class;
                }
                break;

            case BLACK_MESA:
                // different structures used by Black Mesa
                if (sprpVersion == 10 && propStaticSize == 72) {
                    structClass = DStaticPropV10.class;
                } else if (sprpVersion == 11) {
                    if (propStaticSize == 76) {
                        structClass = DStaticPropV11lite.class;
                    } else if (propStaticSize == 80) {
                        structClass = DStaticPropV11.class;
                    }
                }
                break;

            case INSURGENCY:
                // Insurgency is based of the csgo engine branch so we can use DStaticPropV10CSGO
                if (sprpVersion == 10 && propStaticSize == 76) {
                    structClass = DStaticPropV10CSGO.class;
                }
                break;

            default:
                // check for "lite" version of V11 struct in case it applies
                // to a game other than BM (or BM wasn't detected/selected)
                if (sprpVersion == 11 && propStaticSize == 76) {
                    structClass = DStaticPropV11lite.class;
                }
                break;
        }

        // get structure class for the static prop lump version if it's not
        // a special case
        if (structClass == null) {
            try {
                String className = DStaticProp.class.getName();
                structClass = (Class<? extends DStaticProp>) Class.forName(className + "V" + sprpVersion);
            } catch (ClassNotFoundException ex) {
                L.log(Level.WARNING, "Couldn't find static prop struct for version {0}", sprpVersion);
                structClass = null;
            }
        }

        // check if the size is correct
        if (structClass != null) {
            try {
                int propStaticSizeActual = structClass.getDeclaredConstructor().newInstance().getSize();
                if (propStaticSizeActual != propStaticSize) {
                    L.log(Level.WARNING, "Static prop struct size mismatch: expected {0}, got {1} (using {2})",
                            new Object[]{propStaticSize, propStaticSizeActual, structClass.getSimpleName()});
                    structClass = null;
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        // if the correct class is still unknown at this point, fall back to
        // a very basic version that should hopefully work in most situations
        // (note: this will not work well if the struct is based on the V10
        // struct from the Source 2013 or the TF2 Source engine branches,
        // in which case the flags attribute will contain garbage data)
        int numFillBytes;
        if (structClass == null) {
            L.log(Level.WARNING, "Falling back to static prop v4");

            structClass = DStaticPropV4.class;
            numFillBytes = propStaticSize - 56;
        } else {
            numFillBytes = 0;
        }


        Class<? extends DStaticProp> finalStructClass = structClass;

        AbstractPacketContentReader<? extends DStaticProp> staticPropReader = new AbstractPacketContentReader<DStaticProp>() {
            @Override
            protected int packetCount(int remainingBytes) {
                return propStaticCount;
            }

            @Override
            protected DStaticProp readPacket(DataReader in) throws IOException {
                try {
                    DStaticProp sp = finalStructClass.getDeclaredConstructor().newInstance();
                    sp.read(in);

                    if (numFillBytes > 0) {
                        in.seek(numFillBytes, CURRENT);
                    }

                    return sp;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        ByteBuffer buffer = ByteBuffer.allocate(Math.toIntExact(in.remaining()));
        in.readBuffer(buffer);
        return staticPropReader.read(buffer);
    }

    public static class StaticPropData {
        public final List<String> staticPropDict;
        public final List<Integer> staticPropLeaf;
        public final List<? extends DStaticProp> staticProps;

        public StaticPropData() {
            this.staticPropDict = Collections.emptyList();
            this.staticPropLeaf = Collections.emptyList();
            this.staticProps = Collections.emptyList();
        }

        public StaticPropData(List<String> staticPropDict, List<Integer> staticPropLeaf, List<? extends DStaticProp> staticProps) {
            this.staticPropDict = Collections.unmodifiableList(staticPropDict);
            this.staticPropLeaf = Collections.unmodifiableList(staticPropLeaf);
            this.staticProps = Collections.unmodifiableList(staticProps);
        }
    }
}
