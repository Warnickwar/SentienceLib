package org.warnickwar.mindlib.base.identifier;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class AiIdentifier<T> {

    public final String name;

    public AiIdentifier(String name) {
        if (validIdentifier(name)) {
            this.name = name;
        } else  {
            throw new IllegalArgumentException("Non [a-z0-9/._-] Identifier: " + name);
        }
    }

    // At the end of the day, this class is simply a record
    //  of String names for values. Thus, it is okay to sparingly
    //  Change the typecast of the Identifier to be of another type,
    //  ergo an Action to an ActionTree#Branch.
    public <N> AiIdentifier<N> castTo() {
        return new AiIdentifier<>(name);
    }

    @Override
    public @NotNull String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AiIdentifier<?> that = (AiIdentifier<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    public static boolean validIdentifier(String check) {
        for (char c : check.toCharArray()) {
            if (!checkChar(c)) return false;
        }
        return true;
    }

    private static boolean checkChar(char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
    }

}
