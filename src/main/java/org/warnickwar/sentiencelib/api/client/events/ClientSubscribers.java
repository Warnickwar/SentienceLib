package org.warnickwar.sentiencelib.api.client.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.warnickwar.sentiencelib.registries.ModKeybinds;

/**
 * Only runs on the Logical Client;
 * Handles NEOFORGE Bus Gameplay Events
 * @see org.warnickwar.sentiencelib.api.client.SentiencelibClient
 */
public final class ClientSubscribers {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key evt) {
        ModKeybinds.onKeyPress(evt.getKey(), evt.getScanCode(), evt.getAction(),  evt.getModifiers());
    }
}
