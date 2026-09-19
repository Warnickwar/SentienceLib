package org.warnickwar.sentiencelib.api.implementations.actions;

import org.warnickwar.sentiencelib.api.core.actions.IStrategy;

@SuppressWarnings("unused")
public class ArrayStrategy implements IStrategy {

    protected final IStrategy[] strategies;
    protected IStrategy current;
    protected int currentIndex = -1;

    private boolean forceEnd = false;
    private boolean ended = true;

    protected ArrayStrategy(IStrategy[] strategies) {
        this.strategies = strategies;
    }

    @Override
    public void start() {
        current = strategies[0];
        currentIndex = 0;
        ended = false;
        forceEnd = false;

        // Start the first Strategy
        current.start();
    }

    @Override
    public void tick() {
        assert current != null;
        if (current.canPerform()) {
            // Tick and Evaluate the action
            current.tick();
            if (current.isComplete()) {
                current.stop();
                if (++currentIndex >= strategies.length) {
                    ended = true;
                    return;
                }
                current = strategies[currentIndex];
                current.start();
            }
        } else {
            // Force the system to abandon and
            //  cancel the plan through this action
            forceEnd = true;
        }
    }

    @Override
    public void stop() {
        current.stop();

        current = null;
        currentIndex = -1;

        // Reset to allow re-iteration
        forceEnd = false;
        ended = false;
    }

    // If we must forceEnd, end immediately-do NOT continue
    @Override
    public boolean canPerform() {
        return !forceEnd && current != null ? current.canPerform() : strategies[0].canPerform();
    }

    @Override
    public boolean isComplete() {
        return ended;
    }

    public static ArrayStrategy create(IStrategy... strategies) {
        return new ArrayStrategy(strategies);
    }

}
