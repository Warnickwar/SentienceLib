package org.warnickwar.sentience.core.actions;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.warnickwar.sentience.Constants;
import org.warnickwar.sentience.core.Belief;
import org.warnickwar.sentience.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.function.Supplier;

public final class Action {

    private static final Logger LOG = Constants.LOG;

    private static final Supplier<Double> DEFAULT_COST = () -> 1.0D;
    private static final Supplier<Double> DEFAULT_UTILITY = () -> 0.5D;

    private Supplier<Double> cost;
    private Supplier<Double> utility;

    private final HashSet<IdentifiedData<Belief>> preconditions;
    private final HashSet<IdentifiedData<Belief>> effects;

    private boolean forceEnd;
    private boolean shouldLog;

    @SuppressWarnings("DataFlowIssue")
    @NotNull
    private IStrategy strategy = null;

    public Action(IStrategy strategy) {
        cost = DEFAULT_COST;
        utility = DEFAULT_UTILITY;
        preconditions = new HashSet<>();
        effects = new HashSet<>();
        forceEnd = false;
        shouldLog = false;
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

    public boolean shouldLog() {
        return shouldLog;
    }

    public HashSet<IdentifiedData<Belief>> getPreconditions() {
        return new HashSet<>(preconditions);
    }

    public HashSet<IdentifiedData<Belief>> getEffects() {
        return new HashSet<>(effects);
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

    public static class Builder {

        private final Action action;

        public Builder(IStrategy strategy) {
            action = new Action(strategy);
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

        public Builder debug() {
            action.shouldLog = true;
            return this;
        }

        public Action build() {
            return action;
        }
    }
}
