package org.warnickwar.sentience.core.sense;

public class SenseManager extends ImmutableSenseManager {

    public boolean removeSense(SenseType<?> senseType) {
        var sense = this.entries.remove(senseType);
        if (sense != null) {
            sense.onRemove();
            return true;
        }
        return false;
    }

    public <T extends Sense> void addSense(SenseType<T> type, T sense) {
        if (this.entries.put(type, sense) != null) {
            throw new IllegalStateException("Sense type " + type + " already exists in the Sense Manager!");
        };
        sense.onAdd();
    }

}
