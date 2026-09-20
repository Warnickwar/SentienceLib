package org.warnickwar.sentiencelib.api.implementations.actions;

import org.warnickwar.sentiencelib.api.core.actions.IStrategy;

@SuppressWarnings("unused")
public abstract class ArrayStrategy implements IStrategy {

    protected final IStrategy[] strategies;

    private ProcessState processState;

    protected ArrayStrategy(IStrategy[] strategies) {
        this.strategies = strategies;
        this.processState = ProcessState.PROCESSING;
    }

    @Override
    public void start() {
        processState = ProcessState.PROCESSING;
    }

    @Override
    public void stop() {
        processState = ProcessState.PROCESSING;
    }

    // If we end unsuccessfully, fail the action
    @Override
    public boolean canPerform() {
        return processState != ProcessState.END_UNSUCCESSFUL;
    }

    @Override
    public boolean isComplete() {
        return processState == ProcessState.END_SUCCESSFUL;
    }

    protected final void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public static ArrayStrategy sequential(IStrategy... strategies) {
        return new ArrayStrategy.Sequential(strategies);
    }

    // Not called Parallel because it does not run the strategies in parallel,
    //  it still runs them synchronously due to how Agents work.
    //  Simultaneous is the closest to that.
    public static ArrayStrategy simultaneous(IStrategy... strategies) {
        return new ArrayStrategy.Simultaneous(strategies);
    }

    public static class Sequential extends ArrayStrategy {

        protected IStrategy current;
        protected int currentIndex = -1;

        private Sequential(IStrategy[] strategies) {
            super(strategies);
        }

        @Override
        public void start() {
            super.start();
            current = strategies[0];
            currentIndex = 0;

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
                        setProcessState(ProcessState.END_SUCCESSFUL);
                        return;
                    }
                    current = strategies[currentIndex];
                    // Check before starting to ensure this can be performed
                    if (current.canPerform()) {
                        current.start();
                    } else {
                        setProcessState(ProcessState.END_UNSUCCESSFUL);
                    }
                }
            } else {
                // Force the system to abandon and
                //  cancel the plan through this action
                setProcessState(ProcessState.END_UNSUCCESSFUL);
            }
        }

        @Override
        public void stop() {
            super.stop();
            current.stop();

            current = null;
            currentIndex = -1;
        }

        @Override
        public boolean canPerform() {
            return super.canPerform() &&
                current != null ? current.canPerform() : strategies[0].canPerform();
        }

    }

    public static class Simultaneous extends ArrayStrategy {

        Simultaneous(IStrategy[] strategies) {
            super(strategies);
        }

        @Override
        public void start() {
            super.start();

            for (IStrategy strategy : strategies) {
                strategy.start();
            }
        }

        // Always ticks after canPerform
        @Override
        public void tick() {
            super.tick();

            for (IStrategy strategy : strategies) {
                strategy.tick();
            }
        }

        @Override
        public void stop() {
            super.stop();

            for (IStrategy strategy : strategies) {
                strategy.stop();
            }
        }

        @Override
        public boolean canPerform() {
            // Check if all actions can be performed
            for (IStrategy strategy : strategies) {
                if (!strategy.canPerform()) {
                    return false;
                }
            }

            return super.canPerform();
        }

    }

    public enum ProcessState {
        PROCESSING,
        END_SUCCESSFUL,
        END_UNSUCCESSFUL
    }

}
