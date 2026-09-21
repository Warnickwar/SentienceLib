package org.warnickwar.sentiencelib.api.core;

import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class DesireFactory extends FactoryUtil<Map<Identity<Belief>, IdentifiedData<Belief>>, Desire> {

    DesireFactory(Map<Identity<Belief>, IdentifiedData<Belief>> beliefs) {
        super(beliefs);
    }

    // <---> Desire Management <--->

    public DesireFactory register(Identity<Desire> id, Desire belief) {
        return register(IdentifiedData.of(id, belief));
    }

    public DesireFactory register(IdentifiedData<Desire> beliefData) {
        throwIfLocked();
        results.add(beliefData);
        return this;
    }

    // <---> Belief Management <--->

    public Optional<IdentifiedData<Belief>> getBelief(Identity<Belief> beliefType) {
        return availableData.containsKey(beliefType) ?
            Optional.of(availableData.get(beliefType)) :
            Optional.empty();
    }

    public IdentifiedData<Belief> getBeliefSafe(Identity<Belief> beliefType) {
        return availableData.get(beliefType);
    }
}
