package org.warnickwar.sentiencelib.api.core;

import org.warnickwar.sentiencelib.api.core.identifier.Identity;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.sense.ImmutableSenseManager;
import org.warnickwar.sentiencelib.api.core.sense.Sense;
import org.warnickwar.sentiencelib.api.core.sense.SenseType;

import java.util.Optional;

public class BeliefFactory extends FactoryUtil<ImmutableSenseManager, Belief> {

    public BeliefFactory(ImmutableSenseManager senses) {
        super(senses);
    }

    // <---> Belief Management <--->

    public BeliefFactory register(Identity<Belief> id, Belief belief) {
        return register(IdentifiedData.of(id, belief));
    }

    public BeliefFactory register(IdentifiedData<Belief> beliefData) {
        throwIfLocked();
        results.add(beliefData);
        return this;
    }

    // <---> Sense Management <--->

    public boolean hasSense(SenseType<?> senseType) {
        return availableData.hasSense(senseType);
    }

    public <T extends Sense> Optional<T> getSense(SenseType<T> senseType) {
        return availableData.getSense(senseType);
    }

    public <T extends Sense> T getSenseSafe(SenseType<T> senseType) {
        return getSense(senseType).orElseThrow();
    }


}
