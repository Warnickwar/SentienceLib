package org.warnickwar.sentiencelib.api.client.debug;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.client.debug.renderer.DebugSystem;

public final class RegisterDebugSystemEvent extends Event implements IModBusEvent {

    public RegisterDebugSystemEvent() {}

    public void register(@NotNull ResourceLocation id, @NotNull DebugSystem system, boolean defaultState) {
        if (!FMLEnvironment.production) Constants.LOG.debug("Registered new DebugSystem! ID: {}", id);
        DebugManagement.addNewSystem(id, system, defaultState);
    }
}
