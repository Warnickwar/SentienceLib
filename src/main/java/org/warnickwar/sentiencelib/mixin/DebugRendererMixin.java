package org.warnickwar.sentiencelib.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.client.debug.DebugManagement;

// We want the mod debug renderers to be synchronized with Vanilla
//  So to avoid more complicated processing.
@SuppressWarnings("NameDoesntMatchTargetClass")
@Mixin(DebugRenderer.class)
public abstract class DebugRendererMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void sentience$init(Minecraft mc, CallbackInfo ci) {
        DebugManagement.setClient(mc);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void sentience$render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ, CallbackInfo ci) {
        if (Constants.DEBUG) {
            DebugManagement.render(poseStack, bufferSource, camX, camY, camZ);
        }
    }

    @Inject(method = "clear", at = @At("TAIL"))
    public void sentience$clear(CallbackInfo ci) {
        DebugManagement.clear();
    }

}
