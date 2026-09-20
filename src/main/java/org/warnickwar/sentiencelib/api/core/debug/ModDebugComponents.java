package org.warnickwar.sentiencelib.api.core.debug;

import net.minecraft.core.Registry;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.ModRegistries;
import org.warnickwar.sentiencelib.api.core.debug.components.*;

public final class ModDebugComponents {

    public static final DebugComponentType<BasicDetailsComponent> BASIC_DETAILS =
        register(
            "basic_details",
            DebugComponentType.create(BasicDetailsComponent::new)
        );

    public static final DebugComponentType<LevelComponent> LEVEL =
        register(
            "level",
            DebugComponentType.create(LevelComponent::new)
        );

    public static final DebugComponentType<PositionComponent> POSITION =
        register(
            "world_position",
            DebugComponentType.create(PositionComponent::new)
        );

    public static final DebugComponentType<VelocityComponent> VELOCITY =
        register(
            "velocity",
            DebugComponentType.create(VelocityComponent::new)
        );

    public static final DebugComponentType<MobNameComponent> MOB_NAME =
        register(
            "mob_name",
            DebugComponentType.create(MobNameComponent::new)
        );

    public static final DebugComponentType<EntityTypeComponent> MOB_TYPE =
        register(
            "entity_type",
            DebugComponentType.create(EntityTypeComponent::new)
        );

    private static <T extends DebugComponent> DebugComponentType<T> register(String name, DebugComponentType<T> type) {
        return Registry.register(
            ModRegistries.DEBUG_COMPONENTS,
            Constants.loc(name),
            type
        );
    }
}
