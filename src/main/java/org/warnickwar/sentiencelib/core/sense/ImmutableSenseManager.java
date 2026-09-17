package org.warnickwar.sentiencelib.core.sense;

import java.util.*;

public class ImmutableSenseManager {

    protected final Map<SenseType<?>, Sense> entries = new HashMap<>();

    public ImmutableSenseManager() {}

    public Set<SenseType<?>> getRegisteredSenseTypes() {
        return new HashSet<>(this.entries.keySet());
    }

    public boolean hasSense(SenseType<?> senseType) {
        return this.entries.containsKey(senseType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Sense> Optional<T> getSense(SenseType<T> senseType) {
        return (Optional<T>) Optional.ofNullable(this.entries.get(senseType));
    }
}
