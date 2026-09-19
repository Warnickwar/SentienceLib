package org.warnickwar.sentiencelib.api.core.identifier;

import java.util.Objects;

public class IdentifiedData<T> {

    private final Identity<T> identifier;
    private final T value;

    IdentifiedData(Identity<T> identifier, T value) {
        this.identifier = identifier;
        this.value = value;
    }

    public Identity<T> getIdentifier() {
        return identifier;
    }

    public T getValue() {
        return value;
    }

    public boolean isId(Identity<T> identifier) {
        return this.identifier.equals(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiedData<?> aReturn = (IdentifiedData<?>) o;
        return Objects.equals(identifier, aReturn.identifier) && Objects.equals(value, aReturn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, value);
    }

    public static <T> IdentifiedData<T> of(Identity<T> identifier, T value) {
        return new IdentifiedData<>(identifier, value);
    }

}
