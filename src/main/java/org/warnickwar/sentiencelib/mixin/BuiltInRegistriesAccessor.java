package org.warnickwar.sentiencelib.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(BuiltInRegistries.class)
public interface BuiltInRegistriesAccessor {

    @Accessor(value = "LOADERS")
    static Map<ResourceLocation, Supplier<?>> sentience$getLoaders() {
        throw new AssertionError("Accessor application failed");
    }

}
