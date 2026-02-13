package org.warnickwar.mindlib.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Registry.class)
public interface RegistryAccessor {

    @Accessor(value = "LOADERS")
    static Map<ResourceLocation, Supplier<?>> mindlib$getLoaders() {
        throw new AssertionError("Accessor application failed");
    }

}
