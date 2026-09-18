package org.warnickwar.sentiencelib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.debug.DebugInformation;

/**
 * @see DebugInformation
 */
public record S2CDebugInformationPacket(DebugInformation info) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<S2CDebugInformationPacket> TYPE = new Type<>(Constants.loc("debug-information-packet"));

    // Could use a Transformer here, don't care to
    public static final StreamCodec<ByteBuf, S2CDebugInformationPacket> STREAM_CODEC = StreamCodec.composite(
        DebugInformation.STREAM_CODEC, S2CDebugInformationPacket::info,
        S2CDebugInformationPacket::new
    );

    @Override
    @NotNull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
