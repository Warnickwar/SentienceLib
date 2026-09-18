package org.warnickwar.sentiencelib.api.core.debug;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.ModRegistries;

import java.util.Objects;

public abstract class DebugComponent {

    public static final StreamCodec<ByteBuf, DebugComponent> STREAM_CODEC = StreamCodec.of(
        (buff, component) -> {
            ResourceLocation.STREAM_CODEC.encode(buff, Objects.requireNonNull(DebugComponentType.getKey(component.type)));
            component.write(buff);
        },
        buff -> {
            DebugComponentType<?> type = ModRegistries.DEBUG_COMPONENTS.get(ResourceLocation.STREAM_CODEC.decode(buff));
            assert type != null;
            DebugComponent def = type.constructDefault();
            def.read(buff);
            return def;
        }
    );

    private final DebugComponentType<?> type;

    public DebugComponent(@NotNull DebugComponentType<?> type) {
        this.type = type;
    }

    public DebugComponentType<?> getType() {
        return this.type;
    }

    public abstract void write(ByteBuf networkBuffer);

    public abstract void read(ByteBuf networkBuffer);
}
