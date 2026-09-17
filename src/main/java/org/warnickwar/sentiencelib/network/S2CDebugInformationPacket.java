package org.warnickwar.sentiencelib.network;

import net.minecraft.network.FriendlyByteBuf;
import org.warnickwar.sentiencelib.core.debug.DebugInformation;

/**
 * @see DebugInformation
 */
public record S2CDebugInformationPacket(DebugInformation info) {

    public void write(FriendlyByteBuf buffer) {
        this.info.write(buffer);
    }

}
