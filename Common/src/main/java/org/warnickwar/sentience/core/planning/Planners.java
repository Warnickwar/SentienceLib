package org.warnickwar.sentience.core.planning;

import org.slf4j.Logger;
import org.warnickwar.sentience.Constants;
import org.warnickwar.sentience.core.Belief;
import org.warnickwar.sentience.core.Desire;
import org.warnickwar.sentience.core.actions.Action;
import org.warnickwar.sentience.core.actions.ActionPlan;
import org.warnickwar.sentience.core.identifier.IdentifiedData;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *     A static Class holding various default methods provided by the Library.
 * </p>
 * <p>
 *     All available methods within this class follow the contract of {@link PlanBuilder PlanBuilders}.
 * </p>
 */
public final class Planners {

    private final Logger LOGGER = Constants.LOG;

    // <---> Depth-First-Search Pattern <--->

    @Nullable
    public static ActionPlan DFSPlan(HashSet<IdentifiedData<Desire>> desires,
                                     HashSet<IdentifiedData<Action>> actions,
                                     Map<IdentifiedData<Belief>, Boolean> beliefEvaluations,
                                     @Nullable IdentifiedData<Desire> lastDesire) {
        List<IdentifiedData<Desire>> orderedDesires = desires.stream().filter(d -> isDesireIncomplete(d.getValue(), beliefEvaluations)).sorted((d1, d2) -> compareDesires(d1.getValue(), d2.getValue(), lastDesire != null ?
            lastDesire.getValue() :
            null)).collect(Collectors.toCollection(LinkedList::new));

        try {
            for (IdentifiedData<Desire> desire : orderedDesires) {
                Node goalPost = new Node(null, null, desire.getValue().getDesires(), 0);

                if (findPathDFS(goalPost, actions, beliefEvaluations)) {
                    if (goalPost.isLeafDead()) continue;

                    LinkedList<IdentifiedData<Action>> actionStack = new LinkedList<>();
                    Node currentNode = goalPost;
                    while (!currentNode.leaves.isEmpty()) {
                        currentNode.leaves.sort(Planners::compareNodes);
                        currentNode = currentNode.leaves.get(0);
                        actionStack.add(0, currentNode.action);
                    }
                    return new ActionPlan(desire, actionStack, goalPost.appeal);
                }
            }
        } catch (Exception ignored) {

        }


        return null;
    }

    private static boolean findPathDFS(Node parent, HashSet<IdentifiedData<Action>> actions, Map<IdentifiedData<Belief>, Boolean> evals) {
        SortedSet<IdentifiedData<Action>> sorted = new TreeSet<>((d1, d2) -> Planners.compareActions(d1.getValue(), d2.getValue()));
        sorted.addAll(actions);
        for (IdentifiedData<Action> action : sorted) {
            HashSet<IdentifiedData<Belief>> required = new HashSet<>(parent.requiredEffects);
            required.removeIf(belief -> getBeliefResult(belief, evals));

            // Return early if all preconditions are met
            if (required.isEmpty()) {
                return true;
            }

            // Evaluate this action
            if (action.getValue().getEffects().stream().anyMatch(required::contains)) {
                HashSet<IdentifiedData<Belief>> newRequired = new HashSet<>(required);
                newRequired.removeAll(action.getValue().getEffects());
                newRequired.addAll(action.getValue().getPreconditions());

                Node newNode = new Node(parent, action, newRequired, parent.appeal + action.getValue().getAppeal());

                if (findPathDFS(newNode, actions, evals)) {
                    parent.leaves.add(0, newNode);
                    newRequired.removeAll(action.getValue().getPreconditions());
                }

                if (newRequired.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    // <---> A* Search Pattern <--->

    // Why is A* more confusing than DFS???
    // TODO: Create an A* Option for Agent Planning. Aims are Less Overhead and more Performance, but vastly lower efficiency.
    public static ActionPlan AStarPlan(HashSet<IdentifiedData<Desire>> desires,
                                       HashSet<IdentifiedData<Action>> actions,
                                       Map<IdentifiedData<Belief>, Boolean> beliefEvaluations,
                                       @Nullable IdentifiedData<Desire> lastDesire) {
        List<IdentifiedData<Desire>> orderedDesires = desires.stream().filter(d -> isDesireIncomplete(d.getValue(), beliefEvaluations)).sorted((d1, d2) -> compareDesires(d1.getValue(), d2.getValue(), lastDesire != null ?
            lastDesire.getValue() :
            null)).collect(Collectors.toCollection(LinkedList::new));

        try {
            for (IdentifiedData<Desire> desire : orderedDesires) {
                Node goalPost = new Node(null, null, desire.getValue().getDesires(), 0);
                PriorityQueue<Node> queue = new PriorityQueue<>(Planners::compareNodes);
                queue.add(goalPost);
                while (!queue.isEmpty()) {
                    Node currentNode = queue.poll();
                }
            }
        } catch (Exception ignored) {

        }

        return null;
    }

    private static boolean findPathAStar(Set<Node> openSet, HashSet<IdentifiedData<Action>> actions, Map<IdentifiedData<Belief>, Boolean> evals) {


        return false;
    }

    // <---> Utility Functions <--->

    private static boolean isDesireIncomplete(Desire desire, Map<IdentifiedData<Belief>, Boolean> evaluations) {
        return !desire.getDesires().isEmpty() && desire.getDesires().stream().anyMatch(d -> !getBeliefResult(d, evaluations));
    }

    private static int compareDesires(Desire one, Desire two, @Nullable Desire recent) {
        double priority1 = one.getPriority() - (one == recent ? 0.01D : 0D);
        double priority2 = two.getPriority() - (two == recent ? 0.01D : 0D);
        return priority1 <= priority2 ? -1 : 1;
    }

    private static int compareActions(Action one, Action two) {
        return one.getAppeal() <= two.getAppeal() ? -1 : 1;
    }

    private static int compareNodes(Node one, Node two) {
        return one.appeal <= two.appeal ? -1 : 1;
    }

    private static boolean getBeliefResult(IdentifiedData<Belief> belief, Map<IdentifiedData<Belief>, Boolean> evaluations) {
        // Don't use getOrDefault, it will evaluate the Belief for the Default Value before checking the map existence
        if (!evaluations.containsKey(belief)) {
            // Cache so we don't deal with evaluating the value AGAIN, as it can be potentially costly
            evaluations.put(belief, belief.getValue().evaluate());
        }
        return evaluations.get(belief);
    }

    private static class Node {

        @Nullable
        private final Node parent;
        @Nullable
        private final IdentifiedData<Action> action;
        private final HashSet<IdentifiedData<Belief>> requiredEffects;
        private final List<Node> leaves;

        private final double appeal;

        public Node(@Nullable Node parent, @Nullable IdentifiedData<Action> action, HashSet<IdentifiedData<Belief>> requiredEffects, double appeal) {
            this.parent = parent;
            this.action = action;
            this.requiredEffects = requiredEffects;
            this.leaves = new ArrayList<>();
            this.appeal = appeal;
        }

        public boolean isLeafDead() {
            return leaves.isEmpty() && action == null;
        }
    }
}
