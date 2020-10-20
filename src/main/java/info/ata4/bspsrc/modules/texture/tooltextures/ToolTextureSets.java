package info.ata4.bspsrc.modules.texture.tooltextures;

import info.ata4.bsplib.app.SourceAppID;
import info.ata4.bspsrc.modules.texture.tooltextures.definitions.CssToolTextureDefinitions;
import info.ata4.bspsrc.modules.texture.tooltextures.definitions.SourceToolTextureDefinitions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing the set of tooltextures present in each game
 */
public enum ToolTextureSets {

    SOURCE_2013(-1, SourceToolTextureDefinitions.getToolTextureDefinitions()),
    CSS(
            SourceAppID.COUNTER_STRIKE_SOURCE,
            SOURCE_2013.builder()
                    .putToolTextureDefinitions(CssToolTextureDefinitions.getToolTextureDefinitions())
                    .build()
    );

    private final int appId;
    private final Map<String, ToolTextureDefinition> toolTextureDefinitions;

    ToolTextureSets(int appId, Map<String, ToolTextureDefinition> toolTextureDefinitions) {
        this.appId = appId;
        this.toolTextureDefinitions = Collections.unmodifiableMap(new HashMap<>(toolTextureDefinitions));
    }

    private Builder builder() {
        return new Builder(toolTextureDefinitions);
    }

    public static Map<String, ToolTextureDefinition> forGame(int appId) {
        return Arrays.stream(values())
                .filter(toolTextureSets -> toolTextureSets.appId == appId)
                .findAny()
                .orElse(SOURCE_2013).toolTextureDefinitions;
    }

    /**
     * A Builder class for building a set of tooltexture definition
     */
    private static class Builder {

        private final Map<String, ToolTextureDefinition> toolTextureDefinitions = new HashMap<>();

        public Builder(Map<String, ToolTextureDefinition> toolTextureDefinitions) {
            this.toolTextureDefinitions.putAll(toolTextureDefinitions);
        }

        public Builder putToolTextureDefinitions(Map<String, ToolTextureDefinition> toolTextureDefinitions) {
            this.toolTextureDefinitions.putAll(toolTextureDefinitions);
            return this;
        }

        public Map<String, ToolTextureDefinition> build() {
            return new HashMap<>(toolTextureDefinitions);
        }

        public Builder removeToolTextureDefinitions(String... materialNames) {
            toolTextureDefinitions.keySet().removeAll(Arrays.asList(materialNames));
            return this;
        }
    }
}
