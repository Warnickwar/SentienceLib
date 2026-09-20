package org.warnickwar.sentiencelib.api.implementations.senses;

import net.minecraft.world.entity.LivingEntity;
import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;
import org.warnickwar.sentiencelib.api.core.sense.Sense;

public abstract class BasicEntitySense<T extends LivingEntity> extends BasicMemorySense {

    protected final T owner;

    /**
     * @param scanTimer How many ticks should pass before {@link Sense#onTick()} is run once.
     * @param owner The Entity that should be used when considering this Sensor
     * @param memoryManager The Memory Manager to Mutate
     */
    protected BasicEntitySense(int scanTimer, T owner, MemoryManager memoryManager) {
        super(scanTimer, memoryManager);
        this.owner = owner;
    }
}
