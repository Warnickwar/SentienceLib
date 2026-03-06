package org.warnickwar.sentience.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ModKeybinds {

    public static final String CATEGORY_DEBUG = "key.categories.sentience.debug";

    public static final KeyMapping DEBUG_TOGGLE = new KeyMapping(
        "key.sentience.debug_toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        CATEGORY_DEBUG
    );

}
