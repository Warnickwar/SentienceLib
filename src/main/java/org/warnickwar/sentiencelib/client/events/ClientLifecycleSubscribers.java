package org.warnickwar.sentiencelib.client.events;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.client.debug.RegisterDebugSystemEvent;
import org.warnickwar.sentiencelib.api.client.debug.renderer.system.BasicEntityDebugRenderer;
import org.warnickwar.sentiencelib.client.SentiencelibClient;
import org.warnickwar.sentiencelib.gametest.render.TestEntityRenderer;
import org.warnickwar.sentiencelib.registries.ModEntities;
import org.warnickwar.sentiencelib.registries.ModKeybinds;

/**
 * Only runs on the Logical Client;
 * Handles SENTIENCE Bus Lifecycle Events
 * @see SentiencelibClient
 */
public class ClientLifecycleSubscribers {

    @SubscribeEvent
    public static void FMLClientSetup(FMLClientSetupEvent evt) {
        EntityRenderers.register(ModEntities.DEFAULT_TEST, TestEntityRenderer::new);
    }

    // TODO Deprecate this in favor of registering it directly before posting the event
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
