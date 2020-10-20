package info.ata4.bspsrc.modules.texture.tooltextures;

import info.ata4.bsplib.struct.BrushFlag;
import info.ata4.bsplib.struct.SurfaceFlag;

import java.util.*;

public interface ToolTextureDefinition {

    /**
     * @return a set of brush flags that are either required or forbidden to have
     */
    Map<BrushFlag, MatchingType> getBrushFlagsRequirements();

    /**
     * @return a set of surface flags that are either required or forbidden to have
     */
    Map<SurfaceFlag, MatchingType> getSurfaceFlagsRequirements();

    /**
     * @return the surface property the texture has
     */
    String getSurfaceProperty();

    /**
     * A Builder class for {@link ToolTextureDefinition}s
     */
    public class Builder {

        private final Map<BrushFlag, MatchingType> brushFlagsRequirements = new HashMap<>();
        private final Map<SurfaceFlag, MatchingType> surfaceFlagsRequirements = new HashMap<>();
        private String surfaceProperty;

        public Builder() {
            // when no surface property is specified, "default" is used.
            // This SEEMS to be the case for the source engine in general
            this("default");
        }

        public Builder(String surfaceProperty) {
            this.surfaceProperty = Objects.requireNonNull(surfaceProperty);
        }

        public Builder(ToolTextureDefinition definition) {
            this(definition.getSurfaceProperty());
            brushFlagsRequirements.putAll(definition.getBrushFlagsRequirements());
            surfaceFlagsRequirements.putAll(definition.getSurfaceFlagsRequirements());
        }


        public Builder addRequiredBrushFlags(BrushFlag... brushFlags) {
            return addBrushFlagRequirements(brushFlags, MatchingType.REQUIRED);
        }

        public Builder addForbiddenBrushFlags(BrushFlag... brushFlags) {
            return addBrushFlagRequirements(brushFlags, MatchingType.FORBIDDEN);
        }

        public Builder addBrushFlagRequirements(BrushFlag[] brushFlags, MatchingType matchingType) {
            for (BrushFlag brushFlag : brushFlags) {
                brushFlagsRequirements.put(brushFlag, matchingType);
            }
            return this;
        }


        public Builder addRequiredSurfaceFlags(SurfaceFlag... surfaceFlags) {
            return addSurfaceFlagRequirements(surfaceFlags, MatchingType.REQUIRED);
        }

        public Builder addForbiddenSurfaceFlags(SurfaceFlag... surfaceFlags) {
            return addSurfaceFlagRequirements(surfaceFlags, MatchingType.FORBIDDEN);
        }

        public Builder addSurfaceFlagRequirements(SurfaceFlag[] surfaceFlags, MatchingType matchingType) {
            for (SurfaceFlag surfaceFlag : surfaceFlags) {
                surfaceFlagsRequirements.put(surfaceFlag, matchingType);
            }
            return this;
        }


        public Builder removeBrushFlagRequirements(BrushFlag... brushFlags) {
            brushFlagsRequirements.keySet().removeAll(Arrays.asList(brushFlags));
            return this;
        }
        public Builder removeSurfaceFlagRequirements(SurfaceFlag... surfaceFlags) {
            surfaceFlagsRequirements.keySet().removeAll(Arrays.asList(surfaceFlags));
            return this;
        }

        public Builder clearBrushFlagRequirements() {
            brushFlagsRequirements.clear();
            return this;
        }
        public Builder clearSurfaceFlagRequirements() {
            surfaceFlagsRequirements.clear();
            return this;
        }

        public Builder setSurfaceProperty(String surfaceProperty) {
            this.surfaceProperty = Objects.requireNonNull(surfaceProperty);
            return this;
        }

        public ToolTextureDefinition build() {
            return new ToolTextureDefinition() {

                private final Map<SurfaceFlag, MatchingType> surfaceFlagsRequirements =
                        Collections.unmodifiableMap(new HashMap<>(Builder.this.surfaceFlagsRequirements));
                private final Map<BrushFlag, MatchingType> brushFlagsRequirements =
                        Collections.unmodifiableMap(new HashMap<>(Builder.this.brushFlagsRequirements));
                private final String surfaceProperty = Builder.this.surfaceProperty;

                @Override
                public Map<BrushFlag, MatchingType> getBrushFlagsRequirements() {
                    return brushFlagsRequirements;
                }

                @Override
                public Map<SurfaceFlag, MatchingType> getSurfaceFlagsRequirements() {
                    return surfaceFlagsRequirements;
                }

                @Override
                public String getSurfaceProperty() {
                    return surfaceProperty;
                }
            };
        }
    }
}
