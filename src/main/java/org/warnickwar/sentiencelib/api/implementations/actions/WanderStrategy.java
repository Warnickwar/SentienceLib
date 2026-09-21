package org.warnickwar.sentiencelib.api.implementations.actions;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class WanderStrategy<T extends PathfinderMob> extends EntityStrategy<T> {

    protected final double speedMod;

    private boolean startingNew;
    private final PosHelper desiredPos;

    public WanderStrategy(T entity, double speedMod) {
        super(entity);
        this.speedMod = speedMod;
        desiredPos = new PosHelper(0, 0, 0);
        startingNew = true;
    }

    @Override
    public boolean canPerform() {
        if (entity.getNavigation().isStuck()) return false;
        if (entity.hasControllingPassenger()) return false;

        if (startingNew) {
            Vec3 target = DefaultRandomPos.getPos(entity, 10, 7);

            if (target == null) return false;

            desiredPos.x = target.x;
            desiredPos.y = target.y;
            desiredPos.z = target.z;

            startingNew = false;
        }

        return true;
    }

    @Override
    public void start() {
        entity.getNavigation().moveTo(desiredPos.x, desiredPos.y, desiredPos.z, speedMod);
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        startingNew = true;
        desiredPos.x = 0;
        desiredPos.y = 0;
        desiredPos.z = 0;
    }

    @Override
    public boolean isComplete() {
        return entity.getNavigation().isDone() || entity.hasControllingPassenger();
    }

    protected static class PosHelper {
        double x, y, z;

        PosHelper(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

}
