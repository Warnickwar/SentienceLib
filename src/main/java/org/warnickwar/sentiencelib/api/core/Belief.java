package org.warnickwar.sentiencelib.api.core;

import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class Belief {

    private static final Belief belief$TRUE;
    private static final Belief belief$FALSE;

    private static final Supplier<Boolean> ALWAYS_TRUE = () -> true;

    static {
        belief$TRUE = new Belief();
        belief$FALSE = new Belief();
        belief$TRUE.precondition = () -> true;
        belief$FALSE.precondition = () -> false;
    }

    private Supplier<Boolean> precondition = ALWAYS_TRUE;

    Belief() {}

    /**
     * @return Whether this belief is true in the current context
     */
    public boolean evaluate() {
        return precondition.get();
    }

    public static Belief alwaysTrue() {
        return belief$TRUE;
    }

    public static Belief alwaysFalse() {
        return belief$FALSE;
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

    // Why does this need a Builder?
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

