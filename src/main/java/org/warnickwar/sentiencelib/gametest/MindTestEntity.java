package org.warnickwar.sentiencelib.gametest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.*;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.debug.DebugInformation;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;
import org.warnickwar.sentiencelib.api.core.memories.MemoryManager;
import org.warnickwar.sentiencelib.api.core.sense.SenseManager;
import org.warnickwar.sentiencelib.api.implementations.SenseKeys;
import org.warnickwar.sentiencelib.api.implementations.actions.IdleStrategy;
import org.warnickwar.sentiencelib.api.implementations.actions.WanderStrategy;
import org.warnickwar.sentiencelib.api.implementations.senses.HurtSense;

import java.util.Optional;

public final class MindTestEntity extends PathfinderMob {

    private final Agent<MindTestEntity> agent;
    private final MemoryManager memories;

    public MindTestEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        this.memories = new MemoryManager();
        this.agent = Agent.start(this)
            .sensorSetup(this::setupSensors)
            .beliefSetup(this::setupBeliefs)
            .desireSetup(this::setupDesires)
            .actionSetup(this::setupActions)
            .setProfiler(() -> this.level().getProfiler())
            .build();
    }

    private void setupSensors(SenseManager manager) {
        // Just to test Sense Existence
        //  We use the Debugger for this
        manager.addSense(SenseKeys.HURT, new HurtSense(this, this.memories));
    }

    private void setupBeliefs(BeliefFactory factory) {
        factory.register(
            Identity.of("is_recently_hurt_by_entity"),
            Belief
                .start()
                .withPrecondition(() -> memories.filterMemory(MemoryModuleType.HURT_BY_ENTITY, Optional::isPresent))
                .build()
        );

        factory.register(
            Identity.of("is_moving"),
            Belief
                .start()
                .withPrecondition(() -> !this.navigation.isDone())
                .build()
        );

        factory.register(
            Identity.of("nothing"),
            Belief.alwaysFalse()
        );
    }

    private void setupDesires(DesireFactory factory) {
        factory.register(
            Identity.of("idle"),
            Desire
                .start()
                .withBelief(factory.getBeliefSafe(
                    Identity.of("nothing")
                ))
                .withPriority(1.0D)
                .build()
        );

        factory.register(
            Identity.of("wander"),
            Desire
                .start()
                .withBelief(factory.getBeliefSafe(
                    Identity.of("is_moving")
                ))
                .withPriority(1.0D)
                .build()
        );
    }

    private void setupActions(ActionFactory factory) {
        factory.register(
            Identity.of("idle"),
            Action
                .start(IdleStrategy.dynamicTime(this.level().random, 50, 7 * 20))
                .withEffect(factory.getBeliefSafe(Identity.of("nothing")))
                .build()
        );

        factory.register(
            Identity.of("wander"),
            Action
                .start(new WanderStrategy<>(this, 0.5D))
                .withEffect(factory.getBeliefSafe(Identity.of("is_moving")))
                .build()
        );
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        agent.tick();
    }

    @Override
    public boolean save(@NotNull CompoundTag tag) {
        memories.save("memories", tag);
        return super.save(tag);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        memories.load("memories", tag);
        super.load(tag);
    }

    @Override
    protected void sendDebugPackets() {
        DebugInformation.fromEntity(this, agent);
    }

}
