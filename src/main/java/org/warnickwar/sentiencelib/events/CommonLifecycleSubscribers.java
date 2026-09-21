package org.warnickwar.sentiencelib.events;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.ModRegistries;
import org.warnickwar.sentiencelib.client.events.ClientNetworkHandlers;
import org.warnickwar.sentiencelib.gametest.TestEntity;
import org.warnickwar.sentiencelib.network.S2CDebugInformationPacket;
import org.warnickwar.sentiencelib.registries.ModEntities;

/**
 * Runs on both the Logical Client and Server;
 * Handles SENTIENCE Bus Lifecycle Events
 * @see org.warnickwar.sentiencelib.Sentiencelib
 */
public class CommonLifecycleSubscribers {

    @SubscribeEvent
    public static void handleAttributeCreation(EntityAttributeCreationEvent evt) {
        evt.put(ModEntities.DEFAULT_TEST, TestEntity.getDefaultAttributes());
    }

    @SubscribeEvent
    public static void buildNewRegistries(NewRegistryEvent evt) {
        evt.register(ModRegistries.DEBUG_COMPONENTS);
    }

    @SubscribeEvent
    public static void handleRegistration(RegisterEvent evt) {
        if (evt.getRegistryKey().equals(BuiltInRegistries.ENTITY_TYPE.key())) {
            ModEntities.register(evt);
        }
    }

    @SubscribeEvent
    public static void registerNetworkPayloads(RegisterPayloadHandlersEvent evt) {
        final PayloadRegistrar registrar = evt.registrar(Constants.NETWORK.VERSION);

        // Debug System
        registrar.playToClient(
            S2CDebugInformationPacket.TYPE,
            S2CDebugInformationPacket.STREAM_CODEC,
            ClientNetworkHandlers::handleDebugInfo
        );
    }
}
