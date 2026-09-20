package org.warnickwar.sentiencelib.gametest;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.warnickwar.sentiencelib.api.core.*;
import org.warnickwar.sentiencelib.api.core.debug.DebugInformation;
import org.warnickwar.sentiencelib.api.core.sense.SenseManager;

public abstract class MindTestEntity extends PathfinderMob {

    protected final Agent<MindTestEntity> agent;

    protected MindTestEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        this.agent = Agent.start(this)
            .sensorSetup(this::setupSensors)
            .beliefSetup(this::setupBeliefs)
            .desireSetup(this::setupDesires)
            .actionSetup(this::setupActions)
            .setProfiler(() -> this.level().getProfiler())
            .build();
    }

    protected abstract void setupSensors(SenseManager manager);

    protected abstract void setupBeliefs(BeliefFactory factory);

    protected abstract void setupDesires(DesireFactory factory);

    protected abstract void setupActions(ActionFactory factory);

    @Override
    protected void sendDebugPackets() {
        DebugInformation.fromEntity(this, agent);
    }

}
