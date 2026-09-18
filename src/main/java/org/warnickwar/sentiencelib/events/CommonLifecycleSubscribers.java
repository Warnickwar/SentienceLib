package org.warnickwar.sentiencelib.events;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.ModRegistries;
import org.warnickwar.sentiencelib.api.client.events.ClientNetworkHandlers;
import org.warnickwar.sentiencelib.network.S2CDebugInformationPacket;

/**
 * Runs on both the Logical Client and Server;
 * Handles SENTIENCE Bus Lifecycle Events
 * @see org.warnickwar.sentiencelib.Sentiencelib
 */
public class CommonLifecycleSubscribers {

    @SubscribeEvent
    public static void buildNewRegistries(NewRegistryEvent evt) {
        evt.register(ModRegistries.DEBUG_COMPONENTS);
    }

    @SubscribeEvent
    public static void registerNetworkPayloads(RegisterPayloadHandlersEvent evt) {
        final PayloadRegistrar registrar = evt.registrar(Constants.NETWORK.VERSION);
        registrar.playToClient(
            S2CDebugInformationPacket.TYPE,
            S2CDebugInformationPacket.STREAM_CODEC,
            ClientNetworkHandlers::handleDebugInfo
        );
    }
}
