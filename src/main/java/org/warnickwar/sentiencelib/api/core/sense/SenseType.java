package org.warnickwar.sentiencelib.api.core.sense;

import org.warnickwar.sentiencelib.api.core.identifier.Identity;

public class SenseType<T extends Sense> {

    private final Identity<T> id;

    SenseType(Identity<T> id) {
        this.id = id;
    }

    public Identity<T> getIdentifier() {
        return this.id;
    }

    public static <T extends Sense> SenseType<T> of(Identity<T> name) {
        return new SenseType<>(name);
    }

    public static <T extends Sense> SenseType<T> of(String name) {
        return of(Identity.of(name));
    }
}
