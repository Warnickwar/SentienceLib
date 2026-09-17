package org.warnickwar.sentiencelib.core.debug;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.core.Agent;
import org.warnickwar.sentiencelib.core.Desire;
import org.warnickwar.sentiencelib.core.actions.Action;
import org.warnickwar.sentiencelib.core.debug.components.BasicDetailsComponent;
import org.warnickwar.sentiencelib.core.identifier.SenIdentifier;
import org.warnickwar.sentiencelib.registries.ModDebugComponents;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public final class DebugInformation {

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

    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeFloat(timeToLive);
        buffer.writeInt(components.size());
        for (Map.Entry<DebugComponentType<?>, DebugComponent> componentPair : components.entrySet()) {
            var location = DebugComponentType.getKey(componentPair.getKey());
            assert location != null;
            buffer.writeResourceLocation(location);
            componentPair.getValue().write(buffer);
        }
    }

    public static DebugInformation read(FriendlyByteBuf buffer) {
        UUID uuid = buffer.readUUID();
        float timeToLive = buffer.readFloat();
        DebugInformation info = new DebugInformation(uuid, timeToLive);
        int mapSize =  buffer.readInt();
        for (int entry = 0; entry < mapSize; entry++) {
            var location = buffer.readResourceLocation();
            var type = DebugComponentType.getType(location);
            assert type != null;
            DebugComponent component = type.constructDefault();
            component.read(buffer);
            info.components.put(type, component);
        }
        return info;
    }

    public static DebugInformation.Builder start(UUID uuid, float timeForClientRemoval) {
        return new DebugInformation.Builder(uuid, timeForClientRemoval);
    }

    // DEFAULT CONFIGURATIONS

    @SuppressWarnings("resource")
    public static <T extends Entity> void fromEntity(@NotNull T entity, @NotNull Agent<T> agent) {
        DebugInformation.start(entity.getUUID(), DEFAULT_TTL)
            .addComponent(ModDebugComponents.MOB_NAME.construct(comp -> comp.setMobName(entity)))
            .addComponent(ModDebugComponents.MOB_TYPE.construct(comp -> comp.setEntityType(entity.getType())))
            .addComponent(ModDebugComponents.POSITION.construct(comp -> comp.setPosition(entity.position())))
            .addComponent(ModDebugComponents.VELOCITY.construct(comp -> comp.setMomentum(entity.getDeltaMovement())))
            .addComponent(ModDebugComponents.LEVEL.construct(comp -> comp.setLevelId(entity.level().dimension())))
            .addComponent(ModDebugComponents.BASIC_DETAILS.construct(comp -> setupBasicDetails(comp, agent)))
            .send();
    }

    private static void setupBasicDetails(BasicDetailsComponent component, Agent<?> agent) {
        SenIdentifier<Desire> currentDesire = agent.getCurrentPlanInformation().getFirst();
        // If no Desire is Current, there is no current plan
        if (currentDesire == null) return;

        component.setCurrentDesire(currentDesire.name);

        // Collect Actions
        List<SenIdentifier<Action>> actionQueue = agent.getActionPlanIDs();
        for (int i = 0; i < Math.min(5, actionQueue.size()); i++) {
            // Only update Action IDs for queued actions
            SenIdentifier<Action> currentElem = actionQueue.get(i);
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

        // TODO: Packet handling
        public void send() {}
    }
}
