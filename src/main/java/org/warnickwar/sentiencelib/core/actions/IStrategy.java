package org.warnickwar.sentiencelib.core.actions;

/**
 *
 * @since 0.0.1
 * @author Warnickwar
 */
public interface IStrategy {

    /**
     * <p>
     *      What happens when this Strategy is started by an {@link org.warnickwar.sentiencelib.core.Agent Agent}.
     * </p>
     */
    void start();

    /**
     * <p>
     *      What happens every tick while an Action is being executed.
     * </p>
     * <p>
     *      Executes <b>after</b> {@link IStrategy#canPerform()}.
     * </p>
     * @see IStrategy#canPerform()
     */
    void tick();

    /**
     * <p>
     *     What happens when an Action is ended.
     *     This can be something as simple as clearing variables when not needed,
     *     or cleaning up the action.
     * </p>
     * <p>
     *     This will always be the last thing to execute after {@link IStrategy#isComplete()} returns
     *     {@code true}.
     * </p>
     * @see IStrategy#isComplete()
     */
    void stop();

    /**
     * <p>
     *     Whether the Strategy currently running can be executed.
     * </p>
     * <p>
     *     Executes <b>before</b> {@link IStrategy#tick()}.
     * </p>
     * @return whether the current Strategy is complete.
     * @see IStrategy#tick()
     */
    boolean canPerform();

    /**
     * <p>
     *     Whether this Strategy is complete with its execution.
     * </p>
     * @return {@code true} if this strategy is complete, {@code false} otherwise.
     * @see IStrategy#stop()
     */
    boolean isComplete();


}
