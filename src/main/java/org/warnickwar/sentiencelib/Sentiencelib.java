package org.warnickwar.sentiencelib;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.warnickwar.sentiencelib.mixin.BuiltInRegistriesAccessor;

import java.util.Map;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Sentiencelib.MODID)
public class Sentiencelib {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sentiencelib";

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Sentiencelib(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SuppressWarnings("unchecked")
    public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key, RegistryBootstrap<T> bootstrap) {
        MappedRegistry<T> reg = new MappedRegistry<>(key, Lifecycle.stable(), false);
        Map<ResourceLocation, Supplier<?>> loaders = BuiltInRegistriesAccessor.sentience$getLoaders();
        assert loaders != null;
        loaders.put(key.location(), () -> bootstrap.run(reg));
        ResourceKey<Registry<?>> tempKey = (ResourceKey<Registry<?>>) (Object) key;
        Registry.register(
            (Registry<Registry<?>>) BuiltInRegistries.REGISTRY,
            tempKey,
            reg
        );
        return reg;
    }

    @FunctionalInterface
    public interface RegistryBootstrap<T> {
        T run(Registry<T> reg);
    }

}
