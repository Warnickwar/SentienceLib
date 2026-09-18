package org.warnickwar.sentiencelib.api.client.events;

import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.client.debug.RegisterDebugSystemEvent;
import org.warnickwar.sentiencelib.api.client.debug.renderer.system.BasicEntityDebugRenderer;
import org.warnickwar.sentiencelib.registries.ModKeybinds;

/**
 * Only runs on the Logical Client;
 * Handles SENTIENCE Bus Lifecycle Events
 * @see org.warnickwar.sentiencelib.api.client.SentiencelibClient
 */
public class ClientLifecycleSubscribers {

    @SubscribeEvent
    public static void debugRegistration$test(RegisterDebugSystemEvent evt) {
        evt.register(Constants.loc("default-entity-renderer"), new BasicEntityDebugRenderer(), true);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent evt) {
        for (KeyMapping modKey : ModKeybinds.MOD_KEYS) {
            evt.register(modKey);
        }
    }

}
