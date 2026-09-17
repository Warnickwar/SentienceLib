package org.warnickwar.sentiencelib.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.client.renderer.debug.DebugSystem;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;
import org.warnickwar.sentiencelib.Sentiencelib;

public final class ModRegistries {

    public static final Registry<DebugComponentType<?>> DEBUG_COMPONENTS;
    public static final Registry<DebugSystem> DEBUG_RENDER_SYSTEMS;

    static {
        DEBUG_COMPONENTS = Sentiencelib.createRegistry(
            ResourceKey.createRegistryKey(Constants.loc("debug_components")),
            (r) -> ModDebugComponents.BASIC_DETAILS
        );

        DEBUG_RENDER_SYSTEMS = Sentiencelib.createRegistry(
            ResourceKey.createRegistryKey(Constants.loc("debug_systems")),
            (r) -> null
        );
    }
}
