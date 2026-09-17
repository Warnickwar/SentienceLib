package org.warnickwar.sentiencelib.core;

import net.minecraft.world.phys.Vec3;

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

    // These create new Beliefs for the sake of having multiple
    // "Always True" or "Always False" beliefs.
    // This allows for multiple constant Beliefs.
    public static Belief alwaysTrue() {
        Belief res = new Belief();
        res.precondition = ALWAYS_TRUE;
        return res;
    }

    public static Belief alwaysFalse() {
        Belief res = new Belief();
        res.precondition = ALWAYS_FALSE;
        return res;
    }

    public static Belief nearLocation(Supplier<Vec3> positionSupplier, Supplier<Vec3> locationTarget, float distance) {
        var res = new Belief();
        res.precondition = () -> positionSupplier.get().distanceToSqr(locationTarget.get()) < distance;
        return res;
    }

    public static Belief.Builder start() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Belief belief = (Belief) o;
        return Objects.equals(precondition, belief.precondition);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(precondition);
    }

    public static class Builder {
        private final Belief belief = new Belief();

        Builder() {}

        public Builder withPrecondition(Supplier<Boolean> precondition) {
            belief.precondition = Objects.requireNonNull(precondition);
            return this;
        }

        public Belief build() {
            return belief;
        }
    }
}

