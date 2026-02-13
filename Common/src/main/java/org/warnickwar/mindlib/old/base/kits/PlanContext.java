package org.warnickwar.mindlib.old.base.kits;

import org.warnickwar.mindlib.Constants;
import org.warnickwar.mindlib.old.base.IAction;
import org.warnickwar.mindlib.mind.base.*;
import org.warnickwar.mindlib.old.base.*;
import org.warnickwar.mindlib.old.base.agent.MindAgentHolder;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PlanContext<T extends MindAgent<?>> {

    public static final PlanContext<?> NONE = new PlanContext<>(new MindAgent<Object>() {

        private static final Object INSTANCE = new Object();
        @Override
        public Object getOwner() {
            return INSTANCE;
        }

        @Override
        public Vec3 getLocation() {
            return Constants.NULL_LOCATION;
        }

        // TODO: Create a placeholder Level
        @Override
        public Level getLevel() {
            return null;
        }

        // This is so stupid, but whatever.
        @SuppressWarnings("unchecked")
        @Override
        public PlanContext<MindAgent<Object>> collectContext() {
            return (PlanContext<MindAgent<Object>>) PlanContext.NONE;
        }

        @Override
        protected void setupSensors(Map<String, MindSensor> sensors) {}

        @Override
        protected void setupBeliefs(Map<String, MindBelief> beliefs) {}

        @Override
        protected void setupActions(Set<IAction> actions) {}

        @Override
        protected void setupGoals(Set<MindGoal> goals) {}

        @Override
        protected void onPlanFinish() {}
    });

    private static final Logger LOGGER = LogUtils.getLogger();

    private final T agent;
    private final Set<MindAction> actionInjectors;
    private final Set<MindGoal> goalInjectors;

    PlanContext(@NotNull T agent) {
        this.agent = agent;
        this.actionInjectors = new HashSet<>();
        this.goalInjectors = new HashSet<>();
    }

    public T getAgent() {
        return this.agent;
    }

    public void addAction(MindAction actionToConsider) {
        actionInjectors.add(actionToConsider);
    }

    public void addGoal(MindGoal goalToConsider) {
        goalInjectors.add(goalToConsider);
    }

    public Set<MindAction> getActions() {
        return actionInjectors;
    }

    public Set<MindGoal> getGoals() {
        return goalInjectors;
    }

    public PlanContext<T> merge(PlanContext<T> other) {
        if (other.agent != this.agent) {
            warnNonEqualAgent(this.agent, other.agent);
        }
        PlanContext<T> merged = new PlanContext<>(this.agent);
        merged.actionInjectors.addAll(this.actionInjectors);
        merged.goalInjectors.addAll(this.goalInjectors);
        merged.actionInjectors.addAll(other.actionInjectors);
        merged.goalInjectors.addAll(other.goalInjectors);
        return merged;
    }

    public void mergeThis(PlanContext<T> other) {
        if (other.agent != this.agent) {
            warnNonEqualAgent(this.agent, other.agent);
        }
        this.actionInjectors.addAll(other.actionInjectors);
        this.goalInjectors.addAll(other.goalInjectors);
    }

    public static <T extends MindAgent<?>> PlanContext<T> create(T agent) {
        return new PlanContext<>(agent);
    }

    @SuppressWarnings("unchecked")
    public static <T extends MindAgent<?>> PlanContext<T> create(MindAgentHolder holder) {
        T agent = (T) holder.getAgent();
        return PlanContext.create(agent);
    }

    private static void warnNonEqualAgent(MindAgent<?> one, MindAgent<?> two) {
        LOGGER.warn("[MindLib] Merging context of MindAgent {} and MindAgent {}-these are not the same agent!", one, two);
    }

}
