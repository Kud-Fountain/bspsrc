package info.ata4.bspsrc.modules.texture.tooltextures.definitions;

import info.ata4.bsplib.struct.BrushFlag;
import info.ata4.bsplib.struct.SurfaceFlag;
import info.ata4.bspsrc.modules.texture.ToolTexture;
import info.ata4.bspsrc.modules.texture.tooltextures.MatchingType;
import info.ata4.bspsrc.modules.texture.tooltextures.ToolTextureDefinition;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CsgoToolTextureDefinitions implements ToolTextureDefinition {

    GRENADE_CLIP(
            ToolTexture.CSGO_GRENADECLIP,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_CURRENT_90, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    DRONE_CLIP(
            ToolTexture.CSGO_DRONECLIP,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_CURRENT_180, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),

    //Material specific clip textures
    CLIP_CONCRETE(
            ToolTexture.CSGO_CLIP_CONCRETE,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("concrete")
                    .build()
    ),
    CLIP_METAL(
            ToolTexture.CSGO_CLIP_METAL,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("metal")
                    .build()
    ),
    CLIP_WOOD_CRATE(
            ToolTexture.CSGO_CLIP_WOOD_CRATE,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("Wood_Crate")
                    .build()
    ),
    CLIP_DIRT(
            ToolTexture.CSGO_CLIP_DIRT,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("dirt")
                    .build()
    ),
    CLIP_GLASS(
            ToolTexture.CSGO_CLIP_GLASS,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("glassfloor")
                    .build()
    ),
    CLIP_GRASS(
            ToolTexture.CSGO_CLIP_GRASS,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("grass")
                    .build()
    ),
    CLIP_GRAVEL(
            ToolTexture.CSGO_CLIP_GRAVEL,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("gravel")
                    .build()
    ),
    CLIP_METAL_SAND_BARREL(
            ToolTexture.CSGO_CLIP_METAL_SAND_BARREL,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("metal_sand_barrel")
                    .build()
    ),
    CLIP_METALGRATE(
            ToolTexture.CSGO_CLIP_METALGRATE,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("metalgrate")
                    .build()
    ),
    CLIP_METALVEHICLE(
            ToolTexture.CSGO_CLIP_METALVEHICEL,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("metalvehicle")
                    .build()
    ),
    CLIP_PLASTIC(
            ToolTexture.CSGO_CLIP_PLASTIC,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("plastic")
                    .build()
    ),
    CLIP_RUBBER(
            ToolTexture.CSGO_CLIP_RUBBER,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("rubber")
                    .build()
    ),
    CLIP_RUBBERTIRE(
            ToolTexture.CSGO_CLIP_RUBBERTIRE,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("rubbertire")
                    .build()
    ),
    CLIP_SAND(
            ToolTexture.CSGO_CLIP_SAND,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("sand")
                    .build()
    ),
    CLIP_SNOW(
            ToolTexture.CSGO_CLIP_SNOW,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("snow")
                    .build()
    ),
    CLIP_TILE(
            ToolTexture.CSGO_CLIP_TILE,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("tile")
                    .build()
    ),
    CLIP_WOOD(
            ToolTexture.CSGO_CLIP_WOOD,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("wood")
                    .build()
    ),
    CLIP_WOOD_BASKET(
            ToolTexture.CSGO_CLIP_WOOD_BASKET,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .setSurfaceProperty("Wood_Basket")
                    .build()
    );

    private final String materialName;
    private final ToolTextureDefinition toolTextureDefinition;

    CsgoToolTextureDefinitions(String materialName, ToolTextureDefinition toolTextureDefinition) {
        this.materialName = Objects.requireNonNull(materialName);
        this.toolTextureDefinition = Objects.requireNonNull(toolTextureDefinition);
    }

    public String getMaterialName() {
        return materialName;
    }

    @Override
    public Map<BrushFlag, MatchingType> getBrushFlagsRequirements() {
        return toolTextureDefinition.getBrushFlagsRequirements();
    }

    @Override
    public Map<SurfaceFlag, MatchingType> getSurfaceFlagsRequirements() {
        return toolTextureDefinition.getSurfaceFlagsRequirements();
    }

    @Override
    public String getSurfaceProperty() {
        return toolTextureDefinition.getSurfaceProperty();
    }

    public static Map<String, ToolTextureDefinition> getToolTextureDefinitions() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(CsgoToolTextureDefinitions::getMaterialName, Function.identity()));
    }
}
