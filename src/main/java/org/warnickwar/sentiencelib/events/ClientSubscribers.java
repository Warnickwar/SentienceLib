package org.warnickwar.sentiencelib.events;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.registries.ModKeybinds;

@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public final class ClientSubscribers {

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent evt) {
        for (KeyMapping modKey : ModKeybinds.MOD_KEYS) {
            evt.register(modKey);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key evt) {
        ModKeybinds.onKeyPress(evt.getKey(), evt.getScanCode(), evt.getAction(),  evt.getModifiers());
    }
}
