package org.warnickwar.sentiencelib.api.core;

import org.warnickwar.sentiencelib.api.core.identifier.IdentifiedData;

import java.util.HashSet;
import java.util.Set;

class FactoryUtil<Av, Re> {

    protected final Set<IdentifiedData<Re>> results;
    protected final Av availableData;
    private boolean locked;

    protected FactoryUtil(Av available) {
        availableData = available;
        results = new HashSet<>();
        locked = false;
    }

    // Before anyone dare ask, the Register methods are not
    //  defined here due to how the return scope works. I don't want to
    //  bother trying to make it work automatically.

    protected void throwIfLocked() {
        if (locked) throw new IllegalStateException("Cannot alter a locked Factory!");
    }

    // <---> Finalization <--->

    final Set<IdentifiedData<Re>> closeFactory() {
        throwIfLocked();
        locked = true;
        return results;
    }
}
