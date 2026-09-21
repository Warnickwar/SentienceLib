package org.warnickwar.sentiencelib.client.events;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.warnickwar.sentiencelib.api.client.debug.DebugManagement;
import org.warnickwar.sentiencelib.network.S2CDebugInformationPacket;

public class ClientNetworkHandlers {

    public static void handleDebugInfo(S2CDebugInformationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> DebugManagement.handleNewInformation(packet));
    }
}
