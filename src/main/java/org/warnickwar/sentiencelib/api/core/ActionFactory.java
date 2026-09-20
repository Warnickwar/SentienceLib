package org.warnickwar.sentiencelib.api.core;

import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;

import java.util.Map;
import java.util.Optional;

public class ActionFactory extends FactoryUtil<Map<Identity<Desire>, IdentifiedData<Desire>>, Action> {

    public ActionFactory(Map<Identity<Desire>, IdentifiedData<Desire>> available) {
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

    // Desire Accessing

    public Optional<Desire> getDesire(Identity<Desire> key) {
        return availableData.containsKey(key) ?
            Optional.of(availableData.get(key).getValue()) :
            Optional.empty();
    }

    public Desire getDesireSafe(Identity<Desire> key) {
        return availableData.get(key).getValue();
    }

}
