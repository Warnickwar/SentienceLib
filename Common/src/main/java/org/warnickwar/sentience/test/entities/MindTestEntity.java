package org.warnickwar.sentience.test.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.warnickwar.sentience.core.actions.Action;
import org.warnickwar.sentience.core.Agent;
import org.warnickwar.sentience.core.Belief;
import org.warnickwar.sentience.core.Desire;
import org.warnickwar.sentience.core.sense.ImmutableSenseManager;
import org.warnickwar.sentience.core.sense.SenseManager;
import org.warnickwar.sentience.core.identifier.AiIdentifier;
import org.warnickwar.sentience.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MindTestEntity extends PathfinderMob {

    protected final Agent<MindTestEntity> agent;

    protected MindTestEntity(EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        this.agent = new Agent.Builder<>(this, this::position)
            .sensorSetup(this::setupSensors)
            .beliefSetup((senses) -> {
                HashSet<IdentifiedData<Belief>> out = new HashSet<>();
                setupBeliefs(senses, out);
                return out;
            })
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

    protected void setupBeliefs(ImmutableSenseManager senses, Set<IdentifiedData<Belief>> out) {

    }

    protected void setupDesires(Map<AiIdentifier<Belief>, IdentifiedData<Belief>> beliefs, Set<IdentifiedData<Desire>> out) {

    }

    protected void setupActions(Map<AiIdentifier<Desire>, IdentifiedData<Desire>> desires, Set<IdentifiedData<Action>> out) {

    }

}
