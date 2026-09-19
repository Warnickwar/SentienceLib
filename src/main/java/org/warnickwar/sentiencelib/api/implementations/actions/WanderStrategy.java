package org.warnickwar.sentiencelib.api.implementations.actions;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class WanderStrategy<T extends PathfinderMob> extends EntityStrategy<T> {

    protected final double speedMod;

    public WanderStrategy(T entity, double speedMod) {
        super(entity);
        this.speedMod = speedMod;
    }

    @Override
    public boolean canPerform() {
        return !entity.hasControllingPassenger();
    }

    @Override
    public void start() {
        Vec3 target = DefaultRandomPos.getPos(entity, 10, 7);

        if (target == null) {
            entity.getNavigation().stop();
            return;
        }

        entity.getNavigation().moveTo(target.x, target.y, target.z, speedMod);
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
    }

    @Override
    public boolean isComplete() {
        return entity.getNavigation().isDone() || entity.hasControllingPassenger();
    }

}
