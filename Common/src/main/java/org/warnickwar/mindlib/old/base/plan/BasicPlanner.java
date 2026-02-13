package org.warnickwar.mindlib.old.base.plan;

import org.warnickwar.mindlib.old.base.IAction;
import org.warnickwar.mindlib.mind.base.*;
import org.warnickwar.mindlib.old.base.*;
import org.warnickwar.mindlib.old.base.kits.PlanContext;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class BasicPlanner implements IPlanner {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public <T> ActionPlan plan(MindAgent<T> agent, HashSet<MindGoal> goals, @Nullable MindGoal recentGoal) {
        List<MindGoal> ordered = new ArrayList<>();
        PlanContext<MindAgent<T>> context = agent.collectContext();
        // Apply all contextual goals that are available to the Agent.
        // Filter goals, just in case, so we can avoid completing goals that are
        //  Contextual that are already complete.
        ordered.addAll(context.getGoals().stream()
            .filter(BasicPlanner::filterGoal)
            .toList());
        ordered.addAll(goals.stream()
            .filter(BasicPlanner::filterGoal)
            .toList());
        // Order all goals, contextual and otherwise, by priority
        ordered.sort((goal1, goal2) -> compareGoals(goal1, goal2, recentGoal));

        try {
            for (MindGoal goal : ordered) {
                Node goalNode = new Node(null, null, goal.getEffects(), 0);
                HashSet<IAction> availableActions = this.getAgentActions(agent);
                // Add all contextual actions that are available to the Agent.
                availableActions.addAll(context.getActions());
                if (findPath(goalNode, availableActions)) {
                    if (goalNode.isLeafDead()) continue;

                    LinkedList<IAction> actionStack = new LinkedList<>();
                    Node currentNode = goalNode;
                    while (!currentNode.leaves.isEmpty()) {
                        currentNode.leaves.sort(BasicPlanner::compareAppeal);
                        currentNode = currentNode.leaves.get(0);
                        actionStack.add(0, currentNode.action);
                    }

                    return new ActionPlan(goal, actionStack, goalNode.appeal);
                }
            }
        } catch (Exception ignored) {

        }
        LOGGER.error("Could not find Plan for Agent {}!", agent.getOwner().toString());
        return null;
    }

    private static boolean findPath(Node parent, HashSet<IAction> actions) {
        SortedSet<IAction> sorted = new TreeSet<>(BasicPlanner::compareAppeal);
        sorted.addAll(actions);
        for (IAction act : sorted) {
            HashSet<MindBelief> required = new HashSet<>(parent.requiredEffects);
            required.removeIf(MindBelief::evaluate);

            if (required.isEmpty())  {
                return true;
            }

            if (act.getEffects().stream().anyMatch(required::contains)) {
                HashSet<MindBelief> newRequired = new HashSet<>(required);
                newRequired.removeAll(act.getEffects());
                newRequired.addAll(act.getPreconditions());

                Node newNode = new Node(parent, act, newRequired, parent.appeal + act.getAppeal());

                if (findPath(newNode, actions)) {
                    parent.leaves.add(0, newNode);
                    newRequired.removeAll(act.getPreconditions());
                }

                if (newRequired.isEmpty()) return true;
            }
        }
        return false;
    }

    private static boolean filterGoal(@NotNull MindGoal goal) {
        return !goal.getEffects().isEmpty() && goal.getEffects().stream().anyMatch(belief -> !belief.evaluate());
    }

    private static int compareGoals(MindGoal one, MindGoal two, @Nullable MindGoal recentGoal) {
        double priority1 = one == recentGoal ? one.getPriority()-0.01F : one.getPriority();
        double priority2 = two == recentGoal ? two.getPriority()-0.01F : two.getPriority();
        // Move the goal down the line instead of removing at redundant points
        return priority1 <= priority2 ? -1 : 1;
    }

    private static int compareAppeal(IAction act1, IAction act2) {
        double appealOne = act1.getAppeal();
        double appealTwo = act2.getAppeal();
        return appealOne <= appealTwo ? -1 : 1;
    }

    private static int compareAppeal(Node one, Node two) {
        double appealOne = one.appeal;
        double appealTwo = two.appeal;
        return appealOne <= appealTwo ? -1 : 1;
    }

    public static class Node {
        // Useful for debugging
        @Nullable
        private final Node parent;
        @Nullable
        private final IAction action;
        private final HashSet<MindBelief> requiredEffects;
        private final List<Node> leaves;

        private final double appeal;

        public Node(@Nullable Node parent, @Nullable IAction action, HashSet<MindBelief> required, double appeal) {
            this.parent = parent;
            this.action = action;
            this.requiredEffects = required;
            this.leaves = new ArrayList<>();
            this.appeal = appeal;
        }

        public boolean isLeafDead() {
            return leaves.isEmpty() && action == null;
        }

    }

}
