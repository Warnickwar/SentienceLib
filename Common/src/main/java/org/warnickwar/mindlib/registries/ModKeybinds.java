package org.warnickwar.mindlib.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ModKeybinds {

    public static final String CATEGORY_DEBUG = "key.categories.mindlib.debug";

    public static final KeyMapping DEBUG_TOGGLE = new KeyMapping(
        "key.mindlib.debug_toggle",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        CATEGORY_DEBUG
    );

}
