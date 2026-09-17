package org.warnickwar.sentiencelib.core.debug.components;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;

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
    public void write(FriendlyByteBuf networkBuffer) {
        networkBuffer.writeBoolean(entityType != null);
        if (entityType != null) {
            networkBuffer.writeResourceLocation(EntityType.getKey(entityType));
        }
    }

    @Override
    public void read(FriendlyByteBuf networkBuffer) {
        if (networkBuffer.readBoolean()) {
            this.entityType = BuiltInRegistries.ENTITY_TYPE.get(networkBuffer.readResourceLocation());
        }
    }

}
