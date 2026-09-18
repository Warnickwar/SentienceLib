package org.warnickwar.sentiencelib.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

public final class ModRegistries {

    public static final Registry<DebugComponentType<?>> DEBUG_COMPONENTS;

    static {
        DEBUG_COMPONENTS = new RegistryBuilder<DebugComponentType<?>>(ResourceKey.createRegistryKey(Constants.loc("debug_components")))
            .create();
    }
}
