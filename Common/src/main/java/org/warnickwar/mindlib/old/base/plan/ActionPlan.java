package org.warnickwar.mindlib.old.base.plan;

import org.warnickwar.mindlib.old.base.IAction;
import org.warnickwar.mindlib.mind.base.*;
import org.warnickwar.mindlib.old.base.*;

import java.util.LinkedList;

/**
 * <p>
 *     A series of {@link MindAction Actions} which an {@link MindAgent Agent} can use to accomplish
 *     a specific task or goal.
 * </p>
 * <p>
 *     The actions are immutable, and are considered as a {@link java.util.Stack Stack} of Action elements.
 * </p>
 * @see IPlanner
 * @see MindAction
 * @see MindAgent
 * @since 0.0.1
 * @author Warnickwar
 */
@SuppressWarnings("ClassCanBeRecord")
public final class ActionPlan {

    private final MindGoal goal;
    private final LinkedList<IAction> actions;
    private final double totalCost;

    public ActionPlan(MindGoal goal, LinkedList<IAction> actions, double totalCost) {
        this.goal = goal;
        this.actions = actions;
        this.totalCost = totalCost;
    }

    /**
     * @return The {@link MindGoal Goal} that this plan is aimed to accomplish.
     */
    public MindGoal getGoal() {
        return this.goal;
    }

    /**
     * @return The series of {@link MindAction Actions} which will accomplished this plan's {@link MindGoal Goal}.
     * @see ActionPlan#getGoal()
     */
    public LinkedList<IAction> getActions() {
        return this.actions;
    }

    /**
     * @return The cost of every {@link MindAction Action} that was evaluated in this plan.
     */
    public double getTotalCost() { return this.totalCost; }
}
