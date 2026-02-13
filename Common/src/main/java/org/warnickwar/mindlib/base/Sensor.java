package org.warnickwar.mindlib.base;

import org.warnickwar.mindlib.old.base.MindAgent;

/**
 * <p>
 *     An object which executes and evaluates world conditions. This is to be used in {@link Agent Agents}
 *     to allow them to collect information about the world, such as where blocks are and where entities are.
 * </p>
 */
public abstract class Sensor {

    private final int tickTimer;
    private int ticksPassed;

    /**
     * @param tickTimer How many ticks should pass before {@link Sensor#onTick()} is run once.
     */
    protected Sensor(int tickTimer) {
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

    /**
     * What should happen when the Sensor executes.
     */
    protected abstract void onTick();
}
