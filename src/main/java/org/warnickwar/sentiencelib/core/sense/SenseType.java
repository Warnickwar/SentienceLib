package org.warnickwar.sentiencelib.core.sense;

import org.warnickwar.sentiencelib.core.identifier.SenIdentifier;

public class SenseType<T extends Sense> {

    private final SenIdentifier<T> id;

    public SenseType(SenIdentifier<T> id) {
        this.id = id;
    }

    public SenIdentifier<T> getIdentifier() {
        return this.id;
    }
}
