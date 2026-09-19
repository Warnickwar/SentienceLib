package org.warnickwar.sentiencelib.api.core.actions;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.Belief;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public final class Action {

    private static final Supplier<Double> DEFAULT_COST = () -> 1.0D;
    private static final Supplier<Double> DEFAULT_UTILITY = () -> 0.5D;

    private Supplier<Double> cost;
    private Supplier<Double> utility;

    private final Set<IdentifiedData<Belief>> preconditions;
    private final Set<IdentifiedData<Belief>> effects;

    private final Set<IdentifiedData<Belief>> preconditions$Unmodifiable;
    private final Set<IdentifiedData<Belief>> effects$Unmodifiable;

    private boolean forceEnd;

    @NotNull
    private final IStrategy strategy;

    Action(@NotNull IStrategy strategy,
           @NotNull Set<IdentifiedData<Belief>> preconditions,
           @NotNull Set<IdentifiedData<Belief>> effects) {
        this.strategy = strategy;

        cost = DEFAULT_COST;
        utility = DEFAULT_UTILITY;

        this.preconditions = preconditions;
        this.effects = effects;

        preconditions$Unmodifiable = Collections.unmodifiableSet(preconditions);
        effects$Unmodifiable = Collections.unmodifiableSet(effects);

        forceEnd = false;
    }

    public double getCost() {
        // Ensure we never get negative Cost scores to prevent
        //  Edge cases.
        return Math.max(0.0D, cost.get());
    }

    public double getUtility() {
        // Ensure we never get negative Utility scores to prevent
        //  Edge cases.
        return Math.max(0.0D, utility.get());
    }

    public double getAppeal() {
        double util = getUtility();
        return util == 0D ? 0.0D : getCost() / util;
    }

    public Set<IdentifiedData<Belief>> getPreconditions() {
        return preconditions$Unmodifiable;
    }

    public Set<IdentifiedData<Belief>> getEffects() {
        return effects$Unmodifiable;
    }

    public boolean isComplete() {
        return forceEnd || this.strategy.isComplete();
    }

    public void start() {
        forceEnd = false;
        this.strategy.start();
    }

    public void tick() {
        if (this.strategy.canPerform()) {
            this.strategy.tick();
        } else {
            forceEnd = true;
        }
    }

    public void stop() { this.strategy.stop(); }

    public static Action.Builder start(IStrategy strategy) {
        return new Builder(strategy);
    }

    public static class Builder {

        private final Action action;

        Builder(IStrategy strategy) {
            action = new Action(strategy, new HashSet<>(), new HashSet<>());
        }

        public Builder withCost(Supplier<Double> cost) {
            action.cost = cost;
            return this;
        }

        public Builder withCost(double cost) {
            return withCost(() -> cost);
        }

        public Builder withUtility(Supplier<Double> utility) {
            action.utility = utility;
            return this;
        }

        public Builder withUtility(double utility) {
            return withUtility(() -> utility);
        }

        public Builder withPrecondition(IdentifiedData<Belief> belief) {
            action.preconditions.add(belief);
            return this;
        }

        public Builder withEffect(IdentifiedData<Belief> belief) {
            action.effects.add(belief);
            return this;
        }

        public Action build() {
            return action;
        }
    }
}
