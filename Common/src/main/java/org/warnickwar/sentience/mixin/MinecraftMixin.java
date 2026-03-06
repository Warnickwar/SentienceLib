package org.warnickwar.sentience.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.warnickwar.sentience.Constants;
import org.warnickwar.sentience.registries.ModKeybinds;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    void sentience$postClientTick(CallbackInfo ci) {
        while (ModKeybinds.DEBUG_TOGGLE.consumeClick()) {
            Constants.DEBUG = !Constants.DEBUG;
            TextColor res = Constants.DEBUG ? TextColor.parseColor("#55FF55") : TextColor.parseColor("#FF5555");
            Minecraft.getInstance().gui.getChat().addMessage(Component.empty().append("[Debug]: ").append(Component.translatable("debug.sentience.debug_toggle", Constants.DEBUG ? "Enabled" : "Disabled").withStyle(org -> org.withBold(true).withColor(res))));
        }
    }

}
