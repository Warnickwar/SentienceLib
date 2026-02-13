package org.warnickwar.mindlib.old.base.kits;

import net.minecraft.resources.ResourceLocation;

public class BehaviorSlot {

    private final boolean serializable;

    private BehaviorSlot(boolean serializable) {
        this.serializable = serializable;
    }

    public boolean isSerializable() {
        return serializable;
    }

    public static BehaviorSlot create(boolean serializable) {
        return new BehaviorSlot(serializable);
    }

    // TODO: Make Datapack registry,
    //  access registry to get key
    public ResourceLocation getKey() {
        return null;
    }
}
