package org.warnickwar.sentience.core.sense;

import org.warnickwar.sentience.core.identifier.AiIdentifier;

public class SenseType<T extends Sense> {

    private final AiIdentifier<T> id;

    public SenseType(AiIdentifier<T> id) {
        this.id = id;
    }

    public AiIdentifier<T> getIdentifier() {
        return this.id;
    }
}
