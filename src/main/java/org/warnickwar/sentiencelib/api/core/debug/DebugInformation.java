package org.warnickwar.sentiencelib.api.core.debug;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.Agent;
import org.warnickwar.sentiencelib.api.core.Desire;
import org.warnickwar.sentiencelib.api.core.actions.Action;
import org.warnickwar.sentiencelib.api.core.debug.components.BasicDetailsComponent;
import org.warnickwar.sentiencelib.api.core.identifier.Identity;
import org.warnickwar.sentiencelib.network.S2CDebugInformationPacket;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class DebugInformation {

    public static final StreamCodec<ByteBuf, DebugInformation> STREAM_CODEC = StreamCodec.of(
        (buff, info) -> {
            UUIDUtil.STREAM_CODEC.encode(buff, info.uuid);
            ByteBufCodecs.FLOAT.encode(buff, info.timeToLive);
            ByteBufCodecs.INT.encode(buff, info.components.size());
            info.components.forEach((type, data) -> {
                DebugComponent.STREAM_CODEC.encode(buff, data);
            });
        },
        buff -> {
            DebugInformation.Builder builder = new Builder(UUIDUtil.STREAM_CODEC.decode(buff), ByteBufCodecs.FLOAT.decode(buff));
            int mapSize = ByteBufCodecs.INT.decode(buff);
            for (int i = 0; i < mapSize; i++) {
                builder.addComponent(DebugComponent.STREAM_CODEC.decode(buff));
            }
            return builder.debugInformation;
        }
    );

    // 10 seconds
    public static final float DEFAULT_TTL = 10000f;

    final UUID uuid;
    final float timeToLive;
    final Map<DebugComponentType<?>, DebugComponent> components;

    DebugInformation(@NotNull UUID id, float timeToLive) {
        this.uuid = id;
        this.timeToLive = Mth.clamp(timeToLive, 1F, 60F);
        this.components = new Object2ObjectOpenHashMap<>();
    }

    public @NotNull UUID getUUID() {
        return uuid;
    }

    public <T extends DebugComponent> T getComponentSafe(DebugComponentType<T> type) {
        T result = getComponent(type);
        if (result == null) throw new IllegalStateException("Component " + type + " not found");
        return result;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends DebugComponent> T getComponent(DebugComponentType<T> type) {
        return (T) components.get(type);
    }

    public float getTTL() {
        return timeToLive;
    }

    public boolean hasComponent(DebugComponentType<?> type) {
        return components.containsKey(type);
    }

    public static DebugInformation.Builder start(UUID uuid, float timeForClientRemoval) {
        return new DebugInformation.Builder(uuid, timeForClientRemoval);
    }

    // DEFAULT CONFIGURATIONS

    @SuppressWarnings("resource")
    public static <T extends Entity> void fromEntity(@NotNull T entity, @NotNull Agent<T> agent) {
        if (entity.level().isClientSide) return;
        DebugInformation.start(entity.getUUID(), DEFAULT_TTL)
            .addComponent(ModDebugComponents.MOB_NAME.construct(comp -> comp.setMobName(entity)))
            .addComponent(ModDebugComponents.MOB_TYPE.construct(comp -> comp.setEntityType(entity.getType())))
            .addComponent(ModDebugComponents.POSITION.construct(comp -> comp.setPosition(entity.position())))
            .addComponent(ModDebugComponents.VELOCITY.construct(comp -> comp.setMomentum(entity.getDeltaMovement())))
            .addComponent(ModDebugComponents.LEVEL.construct(comp -> comp.setLevelId(entity.level().dimension())))
            .addComponent(ModDebugComponents.BASIC_DETAILS.construct(comp -> setupBasicDetails(comp, agent)))
            .sendToChunk((ServerLevel) entity.level(), entity.chunkPosition());
    }

    private static void setupBasicDetails(BasicDetailsComponent component, Agent<?> agent) {
        Identity<Desire> currentDesire = agent.getCurrentPlanInformation().getFirst();
        // If no Desire is Current, there is no current plan
        if (currentDesire == null) return;

        component.setCurrentDesire(currentDesire.name);

        // Collect Actions
        List<Identity<Action>> actionQueue = agent.getActionPlanIDs();
        for (int i = 0; i < Math.min(5, actionQueue.size()); i++) {
            // Only update Action IDs for queued actions
            Identity<Action> currentElem = actionQueue.get(i);
            component.setQueuedAction(currentElem.name, i);
        }
    }

    public static class Builder {
        private final DebugInformation debugInformation;

        Builder(UUID uuid, float timeForClientRemoval) {
            this.debugInformation = new DebugInformation(uuid, timeForClientRemoval);
        }

        public <T extends DebugComponent> Builder addComponent(T component) {
            debugInformation.components.put(component.getType(), component);
            return this;
        }

        private S2CDebugInformationPacket buildPacket() {
            return new S2CDebugInformationPacket(debugInformation);
        }

        public void sendToAll() {
            PacketDistributor.sendToAllPlayers(buildPacket());
        }

        public void sendToChunk(@NotNull ServerLevel level, @NotNull ChunkPos chunkPos) {
            PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, buildPacket());
        }

        public void sendToLevel(@NotNull ServerLevel level) {
            PacketDistributor.sendToPlayersInDimension(level, buildPacket());
        }

        public void sendToTrackingEntity(@NotNull Entity ent) {
            PacketDistributor.sendToPlayersTrackingEntity(ent, buildPacket());
        }

    }
}
