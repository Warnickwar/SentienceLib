package org.warnickwar.sentiencelib.api.implementations.senses;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NearbyPlayersSense<T extends LivingEntity> extends NearbyEntitiesSense<T> {

    public NearbyPlayersSense(int scanTimer, T owner, MemoryManager memoryManager,
                                 int radiusXZ, int radiusY) {
        super(scanTimer, owner, memoryManager, radiusXZ, radiusY);
    }

    @Override
    protected void onTick() {
        AABB area = owner.getBoundingBox().inflate(getRadiusXZ(), getRadiusY(), getRadiusXZ());

        List<Player> players = owner.level().players().stream()
            .filter(EntitySelector.NO_SPECTATORS)
            .filter(p -> area.intersects(p.getBoundingBox()))
            .sorted(Comparator.comparingDouble(owner::distanceToSqr))
            .collect(Collectors.toList());

        memories.setMemory(MemoryModuleType.NEAREST_PLAYERS, players);
    }

}
