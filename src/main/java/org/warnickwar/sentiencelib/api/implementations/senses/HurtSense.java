package org.warnickwar.sentiencelib.api.implementations.senses;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;

public class HurtSense extends BasicEntitySense<LivingEntity> {

    protected HurtSense(LivingEntity owner, MemoryManager manager) {
        super(1, owner, manager);
    }

    @Override
    protected void onTick() {
        DamageSource recent = owner.getLastDamageSource();
        if (recent != null) {
            memories.setMemory(MemoryModuleType.HURT_BY, recent);
            if (recent.getEntity() instanceof LivingEntity ent) {
                memories.setMemory(MemoryModuleType.HURT_BY_ENTITY, ent);
            }
        } else {
            memories.removeMemory(MemoryModuleType.HURT_BY);
        }
    }

}
