package org.warnickwar.mindlib.base.identifier;

import java.util.Objects;

public class IdentifiedData<T> {

    private final AiIdentifier<T> identifier;
    private final T value;

    IdentifiedData(AiIdentifier<T> identifier, T value) {
        this.identifier = identifier;
        this.value = value;
    }

    public AiIdentifier<T> getIdentifier() {
        return identifier;
    }

    public T getValue() {
        return value;
    }

    public boolean isData(T other) {
        return Objects.equals(value, other);
    }

    public boolean isId(AiIdentifier<T> identifier) {
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

    public static <T> IdentifiedData<T> of(AiIdentifier<T> identifier, T value) {
        return new IdentifiedData<>(identifier, value);
    }

}
