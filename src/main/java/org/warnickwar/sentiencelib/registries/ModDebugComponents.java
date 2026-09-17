package org.warnickwar.sentiencelib.registries;

import net.minecraft.core.Registry;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;
import org.warnickwar.sentiencelib.core.debug.components.*;

public final class ModDebugComponents {

    public static DebugComponentType<BasicDetailsComponent> BASIC_DETAILS =
        register(
            "basic_details",
            DebugComponentType.create(BasicDetailsComponent::new)
        );

    public static DebugComponentType<LevelComponent> LEVEL =
        register(
            "level",
            DebugComponentType.create(LevelComponent::new)
        );

    public static DebugComponentType<PositionComponent> POSITION =
        register(
            "world_position",
            DebugComponentType.create(PositionComponent::new)
        );

    public static DebugComponentType<VelocityComponent> VELOCITY =
        register(
            "velocity",
            DebugComponentType.create(VelocityComponent::new)
        );

    public static DebugComponentType<MobNameComponent> MOB_NAME =
        register(
            "mob_name",
            DebugComponentType.create(MobNameComponent::new)
        );

    public static DebugComponentType<EntityTypeComponent> MOB_TYPE =
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
