package org.warnickwar.sentiencelib.api.core;

import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;

import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class ActionFactory extends FactoryUtil<Map<Identity<Belief>, IdentifiedData<Belief>>, Action> {

    public ActionFactory(Map<Identity<Belief>, IdentifiedData<Belief>> available) {
        super(available);
    }

    // Action Management

    public ActionFactory register(Identity<Action> id, Action action) {
        return register(IdentifiedData.of(id, action));
    }

    public ActionFactory register(IdentifiedData<Action> actionData) {
        throwIfLocked();
        results.add(actionData);
        return this;
    }

    // Belief Accessing

    public Optional<IdentifiedData<Belief>> getBelief(Identity<Belief> key) {
        return availableData.containsKey(key) ?
            Optional.of(availableData.get(key)) :
            Optional.empty();
    }

    public IdentifiedData<Belief> getBeliefSafe(Identity<Belief> key) {
        return availableData.get(key);
    }

}
