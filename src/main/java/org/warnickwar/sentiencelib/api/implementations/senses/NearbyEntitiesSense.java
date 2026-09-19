package org.warnickwar.sentiencelib.api.implementations.senses;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;

import java.util.Comparator;
import java.util.List;

public class NearbyEntitiesSense<T extends LivingEntity> extends BasicEntitySense<T> {

    private final int radiusXZ;
    private final int radiusY;

    public NearbyEntitiesSense(int scanTimer, T owner, MemoryManager memoryManager,
                               int radiusXZ, int radiusY) {
        super(scanTimer, owner, memoryManager);
        this.radiusXZ = radiusXZ;
        this.radiusY = radiusY;
    }

    @Override
    protected void onTick() {
        List<LivingEntity> nearby = owner.level().getEntitiesOfClass(LivingEntity.class,
            owner.getBoundingBox().inflate(radiusXZ, radiusY, radiusXZ));
        nearby.sort(Comparator.comparingDouble(owner::distanceToSqr));

        memories.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, nearby);
        // TODO: Handle Visibility
        // memories.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, ...);
    }

    protected int getRadiusXZ() {
        return radiusXZ;
    }

    protected int getRadiusY() {
        return radiusY;
    }

}
