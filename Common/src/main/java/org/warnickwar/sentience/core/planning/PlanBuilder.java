package org.warnickwar.sentience.core.planning;

import org.warnickwar.sentience.core.Agent;
import org.warnickwar.sentience.core.Belief;
import org.warnickwar.sentience.core.Desire;
import org.warnickwar.sentience.core.actions.Action;
import org.warnickwar.sentience.core.actions.ActionPlan;
import org.warnickwar.sentience.core.identifier.IdentifiedData;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;

/**
 * <p>
 *     An interface supplying the capability of planning for {@link Agent Agents}.
 * </p>
 * <p>
 *     The plan builder contract states that methods provided as a Plan MUST be capable of being run concurrently, as
 *     the Planning system gets delegated to a separate Thread Handler. Having specific objects that are not unique may
 *     result in different issues and undocumented results, including throwing {@link java.util.ConcurrentModificationException}.
 * </p>
 * <p>
 *     The intended manner for which to use this would be using Static methods, but any reasonable method can be used
 *     in replacement as long as it belongs to a thread-safe object, or uses unique sources per agent.
 * </p>
 * @author Warnickwar
 * @see Agent
 * @see ActionPlan
 * @see Desire
 */
@FunctionalInterface
public interface PlanBuilder {
    /**
     * <p>
     *     Create and return a new {@link ActionPlan Plan} for an Agent to execute.
     * </p>
     * @param desires The set of Desires which the Agent would want to accomplish.
     *                  This includes Desires collected from {@link org.warnickwar.sentience.core.context.Context Context}.
     * @param actions The set of Actions which the Agent has at the time of the request.
     *                  This includes Actions collected from {@link org.warnickwar.sentience.core.context.Context Context}.
     * @param beliefEvaluations The map of known Beliefs, and how they evaluate ({@code True} or {@code False}). Used for Thread-safety.
     * @param lastDesire The last Desire which was run by this Agent.
     * @return a new {@link ActionPlan Plan} for the Agent to attempt to execute, or {@code null} if no valid Plan is found.
     */
    ActionPlan plan(HashSet<IdentifiedData<Desire>> desires, HashSet<IdentifiedData<Action>> actions, Map<IdentifiedData<Belief>, Boolean> beliefEvaluations, @Nullable IdentifiedData<Desire> lastDesire);
}
