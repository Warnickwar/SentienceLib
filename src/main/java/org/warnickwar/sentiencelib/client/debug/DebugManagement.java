package org.warnickwar.sentiencelib.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.client.renderer.debug.DebugSystem;
import org.warnickwar.sentiencelib.client.renderer.debug.ModDebugRenderer;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;
import org.warnickwar.sentiencelib.core.debug.DebugInformation;
import org.warnickwar.sentiencelib.network.S2CDebugInformationPacket;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public final class DebugManagement {

    private static final ModDebugRenderer debugRenderer = new ModDebugRenderer();

    // TODO: Menu of enabling certain systems?
    // Render Systems
    private static final Map<ResourceLocation, SystemEntry> registeredSystems = new Object2ObjectOpenHashMap<>();
    private static final Set<SystemEntry> activeSystems = Collections.synchronizedSet(new TreeSet<>(DebugManagement::entryComparator));

    // Archetype Sorting
    private static final Map<Set<DebugComponentType<?>>, Archetype> createdArchetypes = new Object2ObjectOpenHashMap<>();

    // Client
    @Nullable
    private static Minecraft minecraftInstance = null;

    // Debug Information Management
    private static final Map<UUID, InfoEntry> entries = new Object2ObjectOpenHashMap<>();
    private static final Map<UUID, DebugInformation> infoCache = new Object2ObjectOpenHashMap<>();

    // Logic

    // Updates all Information before Rendering
    public static void update(float delta) {
        // Collect and update all Entries
        List<UUID> cachedListToClear = new LinkedList<>();
        for (Map.Entry<UUID, InfoEntry> entry : entries.entrySet()) {
            UUID uuid = entry.getKey();
            InfoEntry value = entry.getValue();
            value.timeLeft -= delta;
            if (value.timeLeft <= 0) {
                cachedListToClear.add(uuid);
            }
        }

        // Handle after iteration checking to ensure
        //  no concurrency errors, if any at all.
        for (UUID uuid : cachedListToClear) {
            entries.remove(uuid);
            infoCache.remove(uuid);
        }

        cachedListToClear.clear();
    }

    // Begins the Render sequence
    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource buffer, double camX, double camY, double camZ) {
        debugRenderer.render(minecraftInstance, poseStack, buffer, camX, camY, camZ, getQueuedRenderInformation());
    }

    // Client Management

    public static void setClient(@NotNull Minecraft client) {
        minecraftInstance = client;
    }

    // Debug Information Management

    public static void handleNewInformation(S2CDebugInformationPacket packet) {
        DebugInformation info = packet.info();
        entries.put(info.getUUID(), new InfoEntry(info));
        infoCache.put(info.getUUID(), info);
    }

    public static void clear() {
        entries.clear();
        infoCache.clear();
    }

    public boolean toggleSystem(ResourceLocation renderId) {
        // If system doesn't exist, return false
        if (!registeredSystems.containsKey(renderId)) return false;

        SystemEntry entry = registeredSystems.get(renderId);
        entry.active = !entry.active;

        if (entry.active) {
            activeSystems.add(entry);
        } else {
            activeSystems.remove(entry);
        }

        return true;
    }

    private void addNewSystem(@NotNull ResourceLocation id, @NotNull DebugSystem system, boolean defaultState) {
        Set<DebugComponentType<?>> requirements = system.requiredComponents();
        SystemEntry res = new SystemEntry(id, system, defaultState, getOrCreateArchetype(requirements));
        registeredSystems.put(id, res);
        if (defaultState) {
            activeSystems.add(res);
        }
    }

    // Archetype Management

    private static Archetype getOrCreateArchetype(Set<DebugComponentType<?>> type) {
        return createdArchetypes.computeIfAbsent(type, Archetype::new);
    }

    // Data Accessors

    /**
     * @return an immutable view of all {@link DebugInformation} currently stored in the Debug System's cache.
     */
    public static Collection<DebugInformation> getAllInfo() { return Collections.unmodifiableCollection(infoCache.values()); }

    /**
     * @return a read-only map of each DebugSystem with valid DebugInformation to render.
     */
    // TODO: Figure out a better way to do this other than getting it every frame.
    static Map<DebugSystem, Collection<DebugInformation>> getQueuedRenderInformation() {
        Map<DebugSystem, Collection<DebugInformation>> results = new HashMap<>();

        activeSystems.forEach(entry -> {
            results.put(entry.system, entry.archetype.patrons.values().stream().map(e -> e.info).collect(Collectors.toUnmodifiableSet()));
        });

        return Collections.unmodifiableMap(results);
    }

    @Nullable
    public static Minecraft getClient() {
        return minecraftInstance;
    }

    private static class InfoEntry {

        UUID identifier;

        DebugInformation info;
        float timeLeft;

        InfoEntry(DebugInformation information) {
            this.info = information;
            this.identifier = information.getUUID();
            this.timeLeft = information.getTTL();
        }

    }

    private static class SystemEntry {

        // Used primarily to render to list
        private final ResourceLocation id;
        private final DebugSystem system;
        private boolean active;

        // Quick access to the Archetype of Valid Information
        private final Archetype archetype;

        SystemEntry(@NotNull ResourceLocation id, @NotNull DebugSystem system, boolean defaultState, @NotNull Archetype archetype) {
            this.id = id;
            this.system = system;
            this.active = defaultState;
            this.archetype = archetype;
        }

    }

    private static class Archetype {
        final Set<DebugComponentType<?>> archetypeComponents;

        Map<UUID, InfoEntry> patrons;

        Archetype(Set<DebugComponentType<?>> archetypeComponents) {
            this.archetypeComponents = archetypeComponents;
        }

        void add(UUID uuid, InfoEntry info) {
            patrons.put(uuid, info);
        }

        // Used when clearing the cache
        void remove(UUID uuid) {
            patrons.remove(uuid);
        }

        boolean isInArchetype(DebugInformation patron) {
            for (DebugComponentType<?> type : archetypeComponents) {
                if (!patron.hasComponent(type))
                    return false;
            }
            return true;
        }
    }

    private static int entryComparator(SystemEntry one, SystemEntry two) {
        return Integer.compare(one.system.renderPriority(), two.system.renderPriority());
    }
}
