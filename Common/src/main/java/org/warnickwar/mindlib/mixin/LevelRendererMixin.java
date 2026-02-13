package org.warnickwar.mindlib.mixin;

import org.joml.Matrix4f;
import org.warnickwar.mindlib.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Inject(method="renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/debug/DebugRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDD)V", shift = At.Shift.AFTER))
    public void mindlib$render(PoseStack poseStack, float $$1, long $$2, boolean $$3, Camera camera, GameRenderer $$5, LightTexture $$6, Matrix4f $$7, CallbackInfo ci) {
        if (Constants.CLIENT.DEBUG_RENDERERS != null) {
            Constants.CLIENT.DEBUG_RENDERERS.render(poseStack, renderBuffers.bufferSource(), camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
        }
    }
}
