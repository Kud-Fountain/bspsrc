package info.ata4.bspsrc.modules.texture.tooltextures;

import info.ata4.bspsrc.modules.texture.tooltextures.definitions.SourceToolTextureDefinitions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the set of tooltextures present in each game
 */
public enum ToolTextureSets {

    SOURCE_2013(-1, SourceToolTextureDefinitions.getToolTextureDefinitions());

    private final int appId;
    private final Map<String, ToolTextureDefinition> toolTextureDefinitions;

    ToolTextureSets(int appId, Map<String, ToolTextureDefinition> toolTextureDefinitions) {
        this.appId = appId;
        this.toolTextureDefinitions = Collections.unmodifiableMap(new HashMap<>(toolTextureDefinitions));
    }

    public static Map<String, ToolTextureDefinition> forGame(int appId) {
        return Arrays.stream(values())
                .filter(toolTextureSets -> toolTextureSets.appId == appId)
                .findAny()
                .orElse(SOURCE_2013).toolTextureDefinitions;
    }
}
