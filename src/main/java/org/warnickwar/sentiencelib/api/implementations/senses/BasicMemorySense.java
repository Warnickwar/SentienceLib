package org.warnickwar.sentiencelib.api.implementations.senses;

import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;
import org.warnickwar.sentiencelib.api.core.sense.Sense;

public abstract class BasicMemorySense extends Sense {

    protected final MemoryManager memories;

    /**
     * @param scanTimer How many ticks should pass before {@link Sense#onTick()} is run once.
     * @param memoryManager The Memory Manager to Mutate
     */
    protected BasicMemorySense(int scanTimer, MemoryManager memoryManager) {
        super(scanTimer);
        this.memories = memoryManager;
    }

}
