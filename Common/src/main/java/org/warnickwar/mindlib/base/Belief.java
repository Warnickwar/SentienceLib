package org.warnickwar.mindlib.base;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public final class Belief {

    private static final Supplier<Boolean> ALWAYS_TRUE = () -> true;
    private static final Supplier<Boolean> ALWAYS_FALSE = () -> false;

    private Supplier<Boolean> precondition = ALWAYS_TRUE;

    Belief() {}

    /**
     * @return Whether this belief is true in the current context
     */
    public boolean evaluate() {
        return precondition.get();
    }

    public static class Builder {
        private final Belief belief = new Belief();

        public Builder() {}

        public Builder withPrecondition(Supplier<Boolean> precondition) {
            belief.precondition = Objects.requireNonNull(precondition);
            return this;
        }

        public Builder alwaysTrue() {
            belief.precondition = ALWAYS_TRUE;
            return this;
        }

        public Builder alwaysFalse() {
            belief.precondition = ALWAYS_FALSE;
            return this;
        }

        public Belief build() {
            return belief;
        }
    }
}
