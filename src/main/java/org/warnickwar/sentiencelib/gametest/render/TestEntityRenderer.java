package org.warnickwar.sentiencelib.gametest.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.gametest.TestEntity;

public class TestEntityRenderer extends EntityRenderer<TestEntity> {


    public TestEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull TestEntity entity, float yaw, float pTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, pTick, poseStack, buffer, packedLight);

        poseStack.pushPose();
        poseStack.translate(-entity.getBbWidth()/2, 0, -entity.getBbWidth()/2);
        poseStack.scale(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        BlockState appearance = entity.isDeadOrDying() ?
            Blocks.REDSTONE_BLOCK.defaultBlockState() :
            Blocks.GOLD_BLOCK.defaultBlockState();
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(appearance, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
        poseStack.popPose();
    }

    @Override
    @NotNull
    public ResourceLocation getTextureLocation(@NotNull TestEntity testEntity) {
        return MissingTextureAtlasSprite.getLocation();
    }

}
