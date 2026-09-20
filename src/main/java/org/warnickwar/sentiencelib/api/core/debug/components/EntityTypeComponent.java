package org.warnickwar.sentiencelib.api.core.debug.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

import javax.annotation.Nullable;

public class EntityTypeComponent extends DebugComponent {

    @Nullable
    private EntityType<?> entityType = null;

    public EntityTypeComponent(DebugComponentType<?> type) {
        super(type);
    }

    public void setEntityType(@NotNull EntityType<?> type) {
        this.entityType = type;
    }

    @Nullable
    public EntityType<?> getEntityType() {
        return this.entityType;
    }

    @Override
    public void write(ByteBuf networkBuffer) {
        boolean write = entityType != null;
        networkBuffer.writeBoolean(write);
        if (write) {
            ResourceLocation.STREAM_CODEC.encode(networkBuffer, EntityType.getKey(entityType));
        }
    }

    @Override
    public void read(ByteBuf networkBuffer) {
        if (networkBuffer.readBoolean()) {
            this.entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.STREAM_CODEC.decode(networkBuffer));
        }
    }

}
