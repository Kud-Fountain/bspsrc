package info.ata4.bspsrc.modules.texture.tooltextures.definitions;

import info.ata4.bsplib.struct.BrushFlag;
import info.ata4.bsplib.struct.SurfaceFlag;
import info.ata4.bspsrc.modules.texture.ToolTexture;
import info.ata4.bspsrc.modules.texture.tooltextures.MatchingType;
import info.ata4.bspsrc.modules.texture.tooltextures.ToolTextureDefinition;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SourceToolTextureDefinitions implements ToolTextureDefinition {
    // General
    BLOCK_BULLETS(
            ToolTexture.BLOCKBULLETS,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_WINDOW, BrushFlag.CONTENTS_TRANSLUCENT)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_TRANS, SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    INVISIBLE(
            ToolTexture.INVIS,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_GRATE, BrushFlag.CONTENTS_TRANSLUCENT)
                    .addForbiddenBrushFlags(BrushFlag.CONTENTS_SOLID)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_TRANS, SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    INVISIBLE_LADDER(
            ToolTexture.INVISLADDER,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_GRATE, BrushFlag.CONTENTS_TRANSLUCENT, BrushFlag.CONTENTS_LADDER)
                    .addForbiddenBrushFlags(BrushFlag.CONTENTS_SOLID)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    NODRAW(
            ToolTexture.NODRAW,
            new Builder()
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    BLOCK_LOS(
            ToolTexture.BLOCKLOS,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_BLOCKLOS, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    BLOCK_LIGHT(
            ToolTexture.BLOCKLIGHT,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_OPAQUE, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    TRIGGER(
            ToolTexture.TRIGGER,
            new Builder()
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NOLIGHT, SurfaceFlag.SURF_TRIGGER)
                    .build()
    ),

    // Optimisation
    HINT(
            ToolTexture.HINT,
            new Builder()
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT, SurfaceFlag.SURF_HINT)
                    .build()
    ),
    SKIP(
            ToolTexture.SKIP,
            new Builder()
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT, SurfaceFlag.SURF_SKIP)
                    .build()
    ),
    AREAPORTAL(
            ToolTexture.AREAPORTAL,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_AREAPORTAL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),

    // Clips
    // CONTENTS_DETAIL in all games?
    // surface property default_silent in all games?
    CLIP(
            ToolTexture.CLIP,
            new Builder("default_silent")
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_PLAYERCLIP, BrushFlag.CONTENTS_MONSTERCLIP, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    // CONTENTS_DETAIL in all games?
    // surface property default_silent in all games?
    NPC_CLIP(
            ToolTexture.NPCCLIP,
            new Builder("default_silent")
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_MONSTERCLIP, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    // CONTENTS_DETAIL in all games?
    // surface property default_silent in all games?
    PLAYER_CLIP(
            ToolTexture.PLAYERCLIP,
            new Builder()
                    .addRequiredBrushFlags(BrushFlag.CONTENTS_PLAYERCLIP, BrushFlag.CONTENTS_DETAIL)
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_NODRAW, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),

    // Sky/fog
    // still requires vbsp source code check!
    // not working at the moment!
//    FOG(
//            ToolTexture.FOG,
//            new Builder()
//                    .addRequiredBrushFlags(BrushFlag.CONTENTS_WINDOW, BrushFlag.CONTENTS_TRANSLUCENT)
//                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_TRANS)
//                    .build()
//    ),
    // surface property default_silent in all games?
    SKYBOX(
            ToolTexture.SKYBOX,
            new Builder("default_silent")
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_SKY, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    ),
    // surface property default_silent in all games?
    SKYBOX_2D(
            ToolTexture.SKYBOX2D,
            new Builder("default_silent")
                    .addRequiredSurfaceFlags(SurfaceFlag.SURF_SKY, SurfaceFlag.SURF_SKY2D, SurfaceFlag.SURF_NOLIGHT)
                    .build()
    );

    private final String materialName;
    private final ToolTextureDefinition toolTextureDefinition;

    SourceToolTextureDefinitions(String materialName, ToolTextureDefinition toolTextureDefinition) {
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

    @SafeVarargs
    private static <T> Set<T> setOf(T... elements) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(elements)));
    }

    public static Map<String, ToolTextureDefinition> getToolTextureDefinitions() {
        return Arrays.stream(values())
                .collect(Collectors.toMap(SourceToolTextureDefinitions::getMaterialName, Function.identity()));
    }
}
