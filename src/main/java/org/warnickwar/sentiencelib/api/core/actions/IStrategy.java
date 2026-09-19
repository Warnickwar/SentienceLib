package org.warnickwar.sentiencelib.api.core.actions;

/**
 * <p>
 *     Strategies define how Agents go about their {@link Action Actions} to accomplish their
 *     current Desire. This allows a lot of flexibility with how to define these actions,
 *     even if it may not affect the current Agent but notifies other Agents in the area.
 * </p>
 * <p>
 *     Agents always complete their current {@link ActionPlan Plan} sequentially, meaning that only
 *     1 Action-and thus, Strategy-is being ticked at any given moment.
 * </p>
 * <p>
 *     Due to how Strategies work, they should ideally be Stateless-When they run, they do their thing.
 *     Implementing a Strategy to work on a State-based system may have
 * </p>
 * @see Action
 * @since 0.0.1
 * @author Warnickwar
 */
public interface IStrategy {

    /**
     * <p>
     *      What happens when this Strategy is started by an {@link org.warnickwar.sentiencelib.api.core.Agent Agent}.
     * </p>
     */
    default void start() {}

    /**
     * <p>
     *      What happens every tick while an Action is being executed.
     * </p>
     * <p>
     *      Executes <b>after</b> {@link IStrategy#canPerform()}.
     * </p>
     * @see IStrategy#canPerform()
     */
    default void tick() {}

    /**
     * <p>
     *     What happens when an Action is ended.
     *     This can be something as simple as clearing variables when not needed,
     *     or cleaning up the action.
     * </p>
     * <p>
     *     This will always be the last thing to execute after {@link IStrategy#isComplete()} returns
     *     {@code true}, or when {@link IStrategy#canPerform()} returns {@code false}.
     * </p>
     * @see IStrategy#canPerform()
     * @see IStrategy#isComplete()
     */
    default void stop() {}

    /**
     * <p>
     *     Whether the Strategy currently running can be executed.
     *     This function is unique, in that if this is ever {@code false} <b>while</b> running the Strategy,
     *     the Agent will consider the Strategy as having "failed".
     * </p>
     * <p>
     *     Failed Actions work uniquely, in that it causes the Agent to discard the <i>entire</i> plan
     *     that was being run, most commonly causing it to replan. Handling this function with the utmost care
     *     is important to get the desire you so wish.
     * </p>
     * <p>
     *     Executes <b>before</b> {@link IStrategy#tick()}.
     * </p>
     * @implNote Be aware this runs EVERY tick.
     * @return whether the current Strategy can be run.
     * @see IStrategy#tick()
     */
    boolean canPerform();

    /**
     * <p>
     *     Whether this Strategy is complete with its execution.
     * </p>
     * <p>
     *     This function is unique in that if this ever returns {@code true}, the Agent will continue to the next
     *     action immediately, calling {@link IStrategy#stop()}
     * </p>
     * @return {@code true} if this strategy is complete, {@code false} otherwise.
     * @see IStrategy#stop()
     */
    boolean isComplete();


}
