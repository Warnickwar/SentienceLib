package org.warnickwar.sentiencelib.api.core.debug.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

public class LevelComponent extends DebugComponent {

    private final StreamCodec<ByteBuf, ResourceKey<Level>> LEVEL_STREAM_CODEC = ResourceKey.streamCodec(Registries.DIMENSION);

    public ResourceKey<Level> levelId = null;

    public LevelComponent(DebugComponentType<?> type) {
        super(type);
    }

    public void setLevelId(@NotNull ResourceKey<Level> level) {
        this.levelId = level;
    }

    public boolean isInLevel() {
        return levelId != null;
    }

    @Nullable
    public ResourceKey<Level> getLevelId() {
        return levelId;
    }

    @Override
    public void write(ByteBuf networkBuffer) {
        boolean write = isInLevel();
        networkBuffer.writeBoolean(write);
        if (write) LEVEL_STREAM_CODEC.encode(networkBuffer, this.levelId);
    }

    @Override
    public void read(ByteBuf networkBuffer) {
        if (networkBuffer.readBoolean()) {
            this.levelId = LEVEL_STREAM_CODEC.decode(networkBuffer);
        }
    }

}
