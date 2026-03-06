package org.warnickwar.sentience.core;

import org.warnickwar.sentience.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.function.Supplier;

public class Desire {

    private static final Supplier<Double> DEFAULT_PRIORITY = () -> 1.0D;

    private final HashSet<IdentifiedData<Belief>> desires = new HashSet<>();
    private Supplier<Double> prioritySupplier = DEFAULT_PRIORITY;

    Desire() {}

    public double getPriority() {
        return prioritySupplier.get();
    }

    // Create a separate version to preserve the original Desires
    public HashSet<IdentifiedData<Belief>> getDesires() {
        return new HashSet<>(desires);
    }

    public static class Builder {
        private final Desire desire;

        public Builder() {
            desire = new Desire();
        }

        public Builder withPriority(double priority) {
            desire.prioritySupplier = () -> priority;
            return this;
        }

        public Builder withPriority(Supplier<Double> prioritySupplier) {
            desire.prioritySupplier = prioritySupplier;
            return this;
        }

        public Builder withBelief(IdentifiedData<Belief> belief) {
            desire.desires.add(belief);
            return this;
        }

        public Desire build() {
            return desire;
        }

    }
}
