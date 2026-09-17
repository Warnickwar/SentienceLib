package org.warnickwar.sentiencelib.core.context;

import org.warnickwar.sentiencelib.core.Agent;
import org.warnickwar.sentiencelib.core.Belief;
import org.warnickwar.sentiencelib.core.Desire;
import org.warnickwar.sentiencelib.core.actions.Action;

public interface IContextProvider {

    /**
     * <p>
     *     This method is called by the Agent's Planner and is used to collect all information on
     *     what an Agent can do. Using conditionals, developers can implement this function
     *     to only allow certain agents to use a goal or action.
     * </p>
     * <p>
     *     {@link Belief Beliefs} for both injected Desires and Actions
     *     do not have to be on the agent's perspective. Rather, it can be from the implementor's perspective-
     *     missing items or low statistics for instance. However, it must then be stated that the Belief on every
     *     {@link Desire Desire} and {@link Action Action} must be thread-safe.
     *     Failing to have a Belief be thread-safe may result in throwing {@link java.util.ConcurrentModificationException} while planning.
     * </p>
     * <p>
     *     In the case of beliefs desiring information from the Agent, or wanting to limit
     *     depending on the implementor, the agent is provided within the context given.
     * </p>
     * @param context The information used to consider what to add to the context while
     *                an Agent is planning.
     * @see Context
     * @see Agent
     * @see Desire
     * @see Action
     */
    void injectContext(Context<Agent<?>> context);
}
