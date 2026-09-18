package org.warnickwar.sentiencelib.test.senses;

import net.minecraft.world.entity.LivingEntity;
import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;
import org.warnickwar.sentiencelib.api.core.sense.Sense;

public class HurtSense extends Sense {

    private final LivingEntity owner;

    protected HurtSense(LivingEntity owner, MemoryManager manager) {
        super(1);
        this.owner = owner;
    }

    @Override
    protected void onAdd() {

    }

    @Override
    protected void onRemove() {

    }

    @Override
    protected void onTick() {}

}
