package org.warnickwar.sentiencelib.core;

import org.warnickwar.sentiencelib.core.identifier.SenIdentifier;
import org.warnickwar.sentiencelib.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.core.sense.ImmutableSenseManager;
import org.warnickwar.sentiencelib.core.sense.Sense;
import org.warnickwar.sentiencelib.core.sense.SenseType;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BeliefFactory {

    final Set<IdentifiedData<Belief>> results = new HashSet<>();
    private final ImmutableSenseManager senses;

    private boolean locked;

    public BeliefFactory(ImmutableSenseManager senses) {
        this.senses = senses;
        locked = false;
    }

    // <---> Belief Management <--->

    public BeliefFactory addBelief(SenIdentifier<Belief> id, Belief belief) {
        if (locked) throw new IllegalStateException("Cannot add Belief to a closed Belief Factory!");
        results.add(IdentifiedData.of(id, belief));
        return this;
    }

    public BeliefFactory addBelief(IdentifiedData<Belief> beliefData) {
        if (locked) throw new IllegalStateException("Cannot add Belief to a closed Belief Factory!");
        results.add(beliefData);
        return this;
    }

    // <---> Sense Management <--->

    public boolean hasSense(SenseType<?> senseType) {
        return senses.hasSense(senseType);
    }

    public <T extends Sense> Optional<T> getSense(SenseType<T> senseType) {
        return senses.getSense(senseType);
    }

    // <---> Finalization <--->

    Set<IdentifiedData<Belief>> closeFactory() {
        if (locked) throw new IllegalStateException("Cannot close an already closed Belief Factory!");
        this.locked = true;
        return results;
    }


}
