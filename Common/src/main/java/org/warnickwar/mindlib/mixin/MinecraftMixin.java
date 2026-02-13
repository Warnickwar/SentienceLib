package org.warnickwar.mindlib.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.warnickwar.mindlib.Constants;
import org.warnickwar.mindlib.registries.ModKeybinds;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    void mindlib$postClientTick(CallbackInfo ci) {
        while (ModKeybinds.DEBUG_TOGGLE.consumeClick()) {
            Constants.DEBUG = !Constants.DEBUG;
            TextColor res = Constants.DEBUG ? TextColor.parseColor("#55FF55") : TextColor.parseColor("#FF5555");
            Minecraft.getInstance().gui.getChat().addMessage(Component.empty().append("[Debug]: ").append(Component.translatable("debug.mindlib.debug_toggle", Constants.DEBUG ? "Enabled" : "Disabled").withStyle(org -> org.withBold(true).withColor(res))));
        }
    }

}
