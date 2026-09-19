package org.warnickwar.sentiencelib.test.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.warnickwar.sentiencelib.api.core.BeliefFactory;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.Agent;
import org.warnickwar.sentiencelib.api.core.Belief;
import org.warnickwar.sentiencelib.api.core.Desire;
import org.warnickwar.sentiencelib.api.core.sense.SenseManager;
import org.warnickwar.sentiencelib.api.core.identifier.SenIdentifier;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MindTestEntity extends PathfinderMob {

    protected final Agent<MindTestEntity> agent;

    protected MindTestEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        this.agent = Agent.start(this)
            .sensorSetup(this::setupSensors)
            .beliefSetup(this::setupBeliefs)
            .desireSetup(beliefs -> {
                HashSet<IdentifiedData<Desire>> out = new HashSet<>();
                setupDesires(beliefs, out);
                return out;
            })
            .actionSetup((map) -> {
                HashSet<IdentifiedData<Action>> out = new HashSet<>();
                setupActions(map, out);
                return out;
            })
            .build();
    }

    protected void setupSensors(SenseManager manager) {

    }

    protected void setupBeliefs(BeliefFactory senses) {

    }

    protected void setupDesires(Map<SenIdentifier<Belief>, IdentifiedData<Belief>> beliefs, Set<IdentifiedData<Desire>> out) {

    }

    protected void setupActions(Map<SenIdentifier<Desire>, IdentifiedData<Desire>> desires, Set<IdentifiedData<Action>> out) {

    }

}
