package org.warnickwar.sentiencelib.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.warnickwar.sentiencelib.Constants;

public class ModKeybinds {

    public static final String CATEGORY_DEBUG = "key.categories.sentiencelib.debug";

    public static final KeyMapping DEBUG_TOGGLE = new KeyMapping(
        "key.sentiencelib.debug_toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        CATEGORY_DEBUG
    );

    public static final KeyMapping[] MOD_KEYS = {DEBUG_TOGGLE};

    public static void onKeyPress(int key, int scancode, int action, int modifiers) {
        while (ModKeybinds.DEBUG_TOGGLE.consumeClick()) {
            Constants.DEBUG = !Constants.DEBUG;
            TextColor res = Constants.DEBUG ? Constants.CHAT_COLOR.Enabled : Constants.CHAT_COLOR.Disabled;
            Minecraft.getInstance().gui.getChat().addMessage(Component.empty().append(Component.translatable("debug.sentiencelib.debug_toggle", Component.literal(Constants.DEBUG ? "ON" : "OFF").withStyle(org -> org.withBold(true).withColor(res)))));
        }
    }

}
