package org.warnickwar.sentiencelib.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.gametest.TestEntity;

public final class ModEntities {

    public static final EntityType<TestEntity> DEFAULT_TEST =
            EntityType.Builder.of(
                TestEntity::new,
                MobCategory.MISC)
                    .noSave()
                    .sized(0.8f,0.8f)
                .build(Constants.loc("basic_test").toString());

    public static void register(RegisterEvent evt) {
        evt.register(
            BuiltInRegistries.ENTITY_TYPE.key(),
            reg -> reg.register(Constants.loc("basic_test"), DEFAULT_TEST)
        );
    }
}
