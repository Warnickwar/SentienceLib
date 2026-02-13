package org.warnickwar.mindlib;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.warnickwar.mindlib.old.base.kits.Behavior;
import org.warnickwar.mindlib.mixin.RegistryAccessor;

public final class Registries {

    public static class KEYS {

        public static final ResourceKey<Registry<Behavior>> BEHAVIORS = ResourceKey.createRegistryKey(new ResourceLocation(Constants.MODID, "behaviors"));

    }

    public static class REGISTRIES {

        @SuppressWarnings("unchecked")
        private static final WritableRegistry<WritableRegistry<?>> ROOT = (WritableRegistry<WritableRegistry<?>>) Registry.REGISTRY;

        public static final Registry<Behavior> BEHAVIORS;

        static {
            BEHAVIORS = create(KEYS.BEHAVIORS, (reg) -> null);
        }

        // Ignore Unreachable code, getLoaders returns the proper map
        //  and thus will not throw an error at runtime
        @SuppressWarnings({"unchecked", "rawtypes", "UnreachableCode"})
        private static <T> Registry<T> create(ResourceKey<Registry<T>> key, RegistryBootstrap<T> bootstrap) {
            MappedRegistry<T> reg = new MappedRegistry<>(key, Lifecycle.experimental(), null);
            assert RegistryAccessor.mindlib$getLoaders() != null;
            RegistryAccessor.mindlib$getLoaders().put(key.location(), () -> bootstrap.run(reg));
            ROOT.register((ResourceKey) key, reg, Lifecycle.experimental());
            return reg;
        }

        public static void load() {}

        @FunctionalInterface
        public interface RegistryBootstrap<T> {
            T run(Registry<T> registry);
        }
    }
}
