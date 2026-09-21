package org.warnickwar.sentiencelib.api.core.identifier;

import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;

import java.util.Objects;

@SuppressWarnings({"ClassCanBeRecord", "unused"})
public final class Identity<T> {

    public final String name;

    Identity(String name) {
        this.name = name;
    }

    // NOTE At the end of the day, this class is simply a record
    //  of String names for values. Thus, it is okay to sparingly
    //  Change the typecast of the Identifier to be of another type,
    @SuppressWarnings("unchecked")
    public <N> Identity<N> castTo() {
        return (Identity<N>) this;
    }

    @Override
    public @NotNull String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Identity<?> that = (Identity<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

//    private static boolean validIdentifier(String check) {
//        for (char c : check.toCharArray()) {
//            if (!checkChar(c)) return false;
//        }
//        return true;
//    }
//
//    private static boolean checkChar(char c) {
//        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.';
//    }

    // TODO: Maybe have a cache system similar to Strings

    public static <T> Identity<T> of(String modid, String name) {
        return new Identity<>(modid + ":" + name);
    }

    public static <T> Identity<T> of(String name) {
        return of(Constants.MODID, name);
    }
}
