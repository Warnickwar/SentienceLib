package org.warnickwar.mindlib.old.base;

import org.warnickwar.mindlib.old.base.plan.ActionPlan;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public interface IPlanner {
    <T> ActionPlan plan(MindAgent<T> agent, HashSet<MindGoal> goals, @Nullable MindGoal recentGoal);

    default <T> ActionPlan plan(MindAgent<T> agent, HashSet<MindGoal> goals) { return plan(agent, goals, null); }

    default HashSet<IAction> getAgentActions(MindAgent<?> agent) {
        return new HashSet<>(agent.actions.values());
    }
}
