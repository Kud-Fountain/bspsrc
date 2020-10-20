package info.ata4.bspsrc.modules.texture.tooltextures;

import java.util.Set;

public enum MatchingType {
    REQUIRED,
    FORBIDDEN;

    public <T> boolean testSet(Set<T> set, T value) {
        switch (this) {
            case REQUIRED: return set.contains(value);
            case FORBIDDEN: return !set.contains(value);
        }

        // Even though this is not reachable, this is sadly needed.
        // Fixable with java 14 which introduces switch expressions
        return false;
    }
}
