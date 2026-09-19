package org.warnickwar.sentiencelib.api.implementations.actions;

import net.minecraft.util.RandomSource;
import org.warnickwar.sentiencelib.api.core.actions.IStrategy;

public abstract class IdleStrategy implements IStrategy {

    protected int ticksToWait = 0;

    private IdleStrategy() {}

    @Override
    public final void start() {
        setRandomWaitTime();
    }

    @Override
    public final void tick() {
        ticksToWait -= 1;
    }

    @Override
    public final boolean canPerform() {
        return true;
    }

    @Override
    public final boolean isComplete() {
        return ticksToWait <= 0;
    }

    protected abstract void setRandomWaitTime();

    public static IdleStrategy staticTime(int timeToWait) {
        return new Static(timeToWait);
    }

    public static IdleStrategy dynamicTime(RandomSource random, int lowerBounds, int higherBounds) {
        return new Dynamic(random, lowerBounds, higherBounds);
    }

    public static final class Static extends IdleStrategy {

        private final int timeToWait;

        private Static(int timeToWait) {
            this.timeToWait = timeToWait;
        }

        @Override
        protected void setRandomWaitTime() {
            ticksToWait = timeToWait;
        }

    }

    public static final class Dynamic extends IdleStrategy {

        private final RandomSource random;
        private final int lower;
        private final int higher;

        private Dynamic(RandomSource random, int lowerBounds, int higherBounds) {
            this.random = random;
            this.lower = lowerBounds;
            this.higher = higherBounds;
        }

        @Override
        protected void setRandomWaitTime() {
            ticksToWait = random.nextInt(lower, higher);
        }

    }

}
