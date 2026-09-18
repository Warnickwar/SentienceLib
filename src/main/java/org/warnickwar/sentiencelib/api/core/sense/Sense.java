package org.warnickwar.sentiencelib.api.core.sense;

import org.warnickwar.sentiencelib.api.core.Agent;

/**
 * <p>
 *     An object which executes and evaluates world conditions. This is to be used in {@link Agent Agents}
 *     to allow them to collect information about the world, such as where blocks are and where entities are.
 * </p>
 */
public abstract class Sense {

    private final int tickTimer;
    private int ticksPassed;

    /**
     * @param tickTimer How many ticks should pass before {@link Sense#onTick()} is run once.
     */
    protected Sense(int tickTimer) {
        this.tickTimer = tickTimer;
        this.ticksPassed = this.tickTimer;
    }

    public final boolean tick() {
        if (ticksPassed++ >= tickTimer) {
            this.onTick();
            this.ticksPassed = 0;
            return true;
        }
        return false;
    }

    protected abstract void onAdd();

    protected abstract void onRemove();

    /**
     * What should happen when the Sense executes.
     */
    protected abstract void onTick();
}
