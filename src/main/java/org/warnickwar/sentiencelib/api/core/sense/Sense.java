package org.warnickwar.sentiencelib.api.core.sense;

import org.warnickwar.sentiencelib.api.core.Agent;

/**
 * <p>
 *     An object which executes and evaluates world conditions. This is to be used in {@link Agent Agents}
 *     to allow them to collect information about the world, such as where blocks are and where entities are.
 * </p>
 */
public abstract class Sense {

    private final int scanTimer;
    private int ticksPassed;

    /**
     * @param scanTimer How many ticks should pass before {@link Sense#onTick()} is run once.
     */
    protected Sense(int scanTimer) {
        this.scanTimer = scanTimer;
        // Execute immediately as the first case
        this.ticksPassed = this.scanTimer;
    }

    public final void tick() {
        if (ticksPassed++ >= scanTimer) {
            this.onTick();
            this.ticksPassed = 0;
        }
    }

    /**
     * What should happen when the Sense is added to an Agent.
     */
    @SuppressWarnings("EmptyMethod")
    protected void onAdd() {}

    /**
     * What should happen when the Sense is removed from an Agent.
     */
    @SuppressWarnings("EmptyMethod")
    protected void onRemove() {}

    /**
     * What should happen when the Sense executes.
     */
    protected abstract void onTick();
}
