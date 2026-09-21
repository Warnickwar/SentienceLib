package org.warnickwar.sentiencelib.api.implementations.actions;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"unused"})
public class MeleeAttackStrategy<T extends PathfinderMob> extends EntityStrategy<T> {

    protected final double speedModifier;
    protected final boolean followOutOfSight;
    protected final int attackInterval;

    private int attackCooldown;
    private int pathRecalculationCooldown;
    private Path currentPath;

    protected double currentTargetX = 0.0D;
    protected double currentTargetY = 0.0D;
    protected double currentTargetZ = 0.0D;

    protected boolean forceEnd;

    @SuppressWarnings("SameParameterValue")
    protected MeleeAttackStrategy(T entity, double speedMod, boolean followOutOfSight, int attackInterval) {
        super(entity);

        speedModifier = speedMod;
        this.followOutOfSight = followOutOfSight;
        this.attackInterval = attackInterval;

        pathRecalculationCooldown = 0;
        attackCooldown = 0;
        forceEnd = false;
    }

    protected MeleeAttackStrategy(T entity, double speedMod, boolean followOutOfSight) {
        this(entity, speedMod, followOutOfSight, 20);
    }

    @Override
    public void start() {
        attackCooldown = 0;
        forceEnd = false;
        entity.setAggressive(true);

        LivingEntity target = entity.getTarget();
        assert target != null;
        this.currentPath = entity.getNavigation().createPath(target, 0);
        if (currentPath == null && !entity.isWithinMeleeAttackRange(target)) {
            forceEnd = true;
        }
    }

    @Override
    public void tick() {

        if (forceEnd) return;

        LivingEntity target = entity.getTarget();
        assert target != null;

        entity.getLookControl().setLookAt(target, 30.0F, 30.0F);

        pathRecalculationCooldown -= 1;

        if ((followOutOfSight || entity.getSensing().hasLineOfSight(target)) &&
                pathRecalculationCooldown <= 0 &&
                target.distanceToSqr(Vec3.atCenterOf(currentPath.getTarget())) >= 1.0D &&
                // Why? I might remove this.
                //  Might be to stutter entity pathfinding when many
                //  are targeting and attacking at once.
                entity.getRandom().nextFloat() < 0.05F) {
            setCurrentTarget(target);
            pathRecalculationCooldown = 4 + entity.getRandom().nextInt(7);

            double distanceToTarget = entity.distanceTo(target);

            if (distanceToTarget > 1024D) {
                pathRecalculationCooldown += 10;
            } else if (distanceToTarget > 256D) {
                pathRecalculationCooldown += 5;
            }

            if (!entity.getNavigation().moveTo(target, speedModifier)) {
                pathRecalculationCooldown += 15;
            }


        }

        this.attackCooldown -= 1;
        checkAndPerformAttack(target);
    }

    @Override
    public void stop() {
        LivingEntity target = entity.getTarget();
        if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            entity.setTarget(null);
        }

        entity.setAggressive(false);
        entity.getNavigation().stop();
    }

    @Override
    public boolean canPerform() {
        // Check if entity has Target
        return forceEnd || isTargetAlive();
    }

    @Override
    public boolean isComplete() {
        return !isTargetAlive();
    }

    private boolean isTargetAlive() {
        LivingEntity target = entity.getTarget();
        return target != null &&
            target.isAlive();
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (!canPerformAttack(target)) return;

        attackCooldown = attackInterval;

        entity.swing(InteractionHand.MAIN_HAND);
        entity.doHurtTarget(target);
    }

    protected boolean canPerformAttack(LivingEntity target) {
        return attackCooldown <= 0 &&
            entity.isWithinMeleeAttackRange(target) &&
            entity.getSensing().hasLineOfSight(target);
    }

    private void setCurrentTarget(LivingEntity target) {
        currentTargetX = target.getX();
        currentTargetY = target.getY();
        currentTargetZ = target.getZ();
    }

}
