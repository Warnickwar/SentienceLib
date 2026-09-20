package org.warnickwar.sentiencelib.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.gametest.MindTestEntity;

public final class ModEntities {

    // TODO: Set this up
    @SuppressWarnings("DataFlowIssue")
    public static final EntityType<MindTestEntity> DEFAULT_TEST =
            EntityType.Builder.of(
                (EntityType<MindTestEntity> type, Level level) -> null,
                MobCategory.MISC)
                    .noSave()
                    .sized(0.8f,0.8f)
                .build(Constants.loc("basic_test").toString());

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> type) {
        ResourceLocation key = Constants.loc(name);
        return Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            key,
            type.build(key.toString())
        );
    }

    public static void register(RegisterEvent evt) {
        evt.register(
            BuiltInRegistries.ENTITY_TYPE.key(),
            reg -> reg.register(Constants.loc("basic_test"), DEFAULT_TEST)
        );
    }
}
