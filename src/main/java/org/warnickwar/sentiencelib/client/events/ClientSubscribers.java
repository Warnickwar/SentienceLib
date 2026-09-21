package org.warnickwar.sentiencelib.client.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import org.warnickwar.sentiencelib.client.SentiencelibClient;
import org.warnickwar.sentiencelib.registries.ModKeybinds;

/**
 * Only runs on the Logical Client;
 * Handles NEOFORGE Bus Gameplay Events
 * @see SentiencelibClient
 */
public final class ClientSubscribers {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key evt) {
        ModKeybinds.onKeyPress(evt.getKey(), evt.getScanCode(), evt.getAction(),  evt.getModifiers());
    }
}
