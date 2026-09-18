package org.warnickwar.sentiencelib.api.core.identifier;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// A simple implementation mimicking the ResourceLocation, only that it can be typed.
// It is prefixed with Sen- as to not intervene with Fabric's Identifiers.
@SuppressWarnings("ClassCanBeRecord")
public final class SenIdentifier<T> {

    public final String name;

    public SenIdentifier(String name) {
        if (validIdentifier(name)) {
            this.name = name;
        } else  {
            throw new IllegalArgumentException("Non [a-z0-9/._-] Identifier: " + name);
        }
    }

    // At the end of the day, this class is simply a record
    //  of String names for values. Thus, it is okay to sparingly
    //  Change the typecast of the Identifier to be of another type,
    @SuppressWarnings("unchecked")
    public <N> SenIdentifier<N> castTo() {
        return (SenIdentifier<N>) this;
    }

    @Override
    public @NotNull String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SenIdentifier<?> that = (SenIdentifier<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    private static boolean validIdentifier(String check) {
        for (char c : check.toCharArray()) {
            if (!checkChar(c)) return false;
        }
        return true;
    }

    private static boolean checkChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

}
