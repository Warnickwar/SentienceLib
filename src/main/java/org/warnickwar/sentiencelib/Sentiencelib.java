package org.warnickwar.sentiencelib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.warnickwar.sentiencelib.api.client.SentiencelibClient;
import org.warnickwar.sentiencelib.events.CommonLifecycleSubscribers;

/**
 * If you are looking for the SentienceLib Client, see {@link SentiencelibClient}
 * @see SentiencelibClient
 */
@Mod(Constants.MODID)
public class Sentiencelib {

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Sentiencelib(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
//        modEventBus.addListener(this::commonSetup);
        modEventBus.register(CommonLifecycleSubscribers.class);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
