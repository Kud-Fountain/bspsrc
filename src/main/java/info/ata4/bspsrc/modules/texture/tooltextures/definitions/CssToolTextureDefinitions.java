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

public enum CssToolTextureDefinitions implements ToolTextureDefinition {
    // Same as standard source2013 but doesn't have any surface flags
    CLIP(
            ToolTexture.CLIP,
            new Builder(SourceToolTextureDefinitions.CLIP)
                    .clearSurfaceFlagRequirements()
                    .build()
    ),
    // Same as standard source2013 but doesn't have any surface flags
    NPC_CLIP(
            ToolTexture.NPCCLIP,
            new Builder(SourceToolTextureDefinitions.NPC_CLIP)
                    .clearSurfaceFlagRequirements()
                    .build()
    ),
    // Same as standard source2013 but doesn't have any surface flags
    PLAYER_CLIP(
            ToolTexture.PLAYERCLIP,
            new Builder(SourceToolTextureDefinitions.PLAYER_CLIP)
                    .clearSurfaceFlagRequirements()
                    .build()
    ),;

    private final String materialName;
    private final ToolTextureDefinition toolTextureDefinition;

    CssToolTextureDefinitions(String materialName, ToolTextureDefinition toolTextureDefinition) {
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
                .collect(Collectors.toMap(CssToolTextureDefinitions::getMaterialName, Function.identity()));
    }
}
