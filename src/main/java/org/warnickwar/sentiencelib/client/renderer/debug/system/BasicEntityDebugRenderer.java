package org.warnickwar.sentiencelib.client.renderer.debug.system;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Transformation;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.client.renderer.debug.DebugSystem;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;
import org.warnickwar.sentiencelib.core.debug.DebugInformation;
import org.warnickwar.sentiencelib.core.debug.components.*;
import org.warnickwar.sentiencelib.registries.ModDebugComponents;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BasicEntityDebugRenderer extends DebugSystem {

    private static final double RENDER_RANGE = 30.0D;
    private static final float ACTION_TEXT_SCALE = 0.02F;
    private static final float GOAL_TEXT_SCALE = 0.03F;

    private static final double RAYCAST_DISTANCE = 8;

    private UUID lastLookedAtUuid;

    @Override
    public void preRender(Collection<DebugInformation> validInformationComponents) {
        Entity camEntity = Minecraft.getInstance().cameraEntity;
        assert camEntity != null;
        Vec3 eyePosition = camEntity.getEyePosition();
        Vec3 viewVector = camEntity.getViewVector(1.0F).scale(RAYCAST_DISTANCE);

        // Render Setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
//        RenderSystem.disableTexture();

        for (DebugInformation info : validInformationComponents) {
            AABB infoAABB = AABB.ofSize(info.getComponentSafe(ModDebugComponents.POSITION).getPosition(), 0.75, 0.75, 0.75).expandTowards(viewVector);
            if (hitsAABB(eyePosition, viewVector, infoAABB, RAYCAST_DISTANCE * RAYCAST_DISTANCE)) {
                lastLookedAtUuid = info.getUUID();
                break;
            }
        }
    }

    @Override
    public void render(Minecraft client, DebugInformation current, PoseStack poseStack, MultiBufferSource.BufferSource buffer, double camX, double camY, double camZ) {
        LevelComponent level = current.getComponentSafe(ModDebugComponents.LEVEL);
        assert client.level != null;

        // Ignore rendering if not in our dimension for some reason.
        if (client.level.dimension() != level.levelId) return;

        // Don't render if too far away
        PositionComponent pos = current.getComponentSafe(ModDebugComponents.POSITION);

        // Consider Velocity if supplied
        VelocityComponent velocity = current.getComponent(ModDebugComponents.VELOCITY);

        // get Partial Tick
        float partialTick = getCurrentPartialTick(client);

        Vec3 resPos = pos.getPosition();
        if (velocity != null) {
            Vec3 velocityVector = velocity.getMomentum();
            resPos = resPos.add(velocityVector.x * partialTick, velocityVector.y * partialTick, velocityVector.z * partialTick);
        }

        if (resPos.distanceToSqr(camX, camY, camZ) >= RENDER_RANGE * RENDER_RANGE) {
            return;
        }

        // Render normally according to specifications
        boolean isSelected = lastLookedAtUuid == current.getUUID();
        // Done to properly render the stack
        int offset = 0;

        EntityTypeComponent type = current.getComponentSafe(ModDebugComponents.MOB_TYPE);
        BasicDetailsComponent mindDetails = current.getComponentSafe(ModDebugComponents.BASIC_DETAILS);

        MobNameComponent name = current.getComponent(ModDebugComponents.MOB_NAME);

        EntityType<?> entityType = type.getEntityType();
        assert entityType != null;

        renderTextOver(client, client.gameRenderer.getMainCamera(), resPos, poseStack, buffer,
            offset++, entityType.toString(), Constants.TEXT_COLOR.WHITE, 0.03F);

        // Render a custom name as well if the Entity has one
        if (name != null) {
            renderTextOver(client, client.gameRenderer.getMainCamera(), resPos, poseStack, buffer,
                offset++, name.getMobName(), Constants.TEXT_COLOR.WHITE, 0.03F);
        }

        // Display Actions if Selected
        if (isSelected) {
            String[] currentActions = mindDetails.getCurrentActions();
            for (int i = 4; i >= 0; i--) {
                int color = i == 0 ? Constants.TEXT_COLOR.WHITE : Constants.TEXT_COLOR.ORANGE;
                renderTextOver(client, client.gameRenderer.getMainCamera(), resPos, poseStack, buffer,
                    offset++, i + ": " + currentActions[i], color, ACTION_TEXT_SCALE);
            }
        }

        // Always render current Desire
        renderTextOver(client, client.gameRenderer.getMainCamera(), resPos, poseStack, buffer,
            offset++, mindDetails.getCurrentDesire(), mindDetails.hasDesire() ? Constants.TEXT_COLOR.GREEN : Constants.TEXT_COLOR.RED, GOAL_TEXT_SCALE);
    }

    @Override
    public void onDeselect() {
        lastLookedAtUuid = null;
    }

    @Override
    public Set<DebugComponentType<?>> requiredComponents() {
        return Set.of(
            ModDebugComponents.MOB_TYPE,
            ModDebugComponents.LEVEL,
            ModDebugComponents.POSITION,
            ModDebugComponents.BASIC_DETAILS
        );
    }

    private void renderTextOver(Minecraft client, Camera camera, Position position, PoseStack poseStack, MultiBufferSource.BufferSource buffer,
                                int offset, String text, int color, float scale) {
        if (!camera.isInitialized()) return;

        Position camPos = camera.getPosition();
        Font font = client.font;

        poseStack.pushPose();
        poseStack.translate((position.x() - camPos.x()),(((position.y() + 2.4D + offset * 0.25D) - camPos.y()) + 0.07F), (position.z() - camPos.z()));
        poseStack.mulPose(camera.rotation());
        poseStack.scale(scale, -scale, scale);

//        RenderSystem.enableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);

        poseStack.scale(-1.0F, 1.0F, 1.0F);

        RenderSystem.applyModelViewMatrix();

        float finalScale = (-font.width(text)) / 2.0F;
        finalScale -= 0.5F / scale;

        float backgroundOpacity = client.options.getBackgroundOpacity(0.25F);
        int resultBackgroundOpacity = (int)(backgroundOpacity * 255.0F) << 24;

        font.drawInBatch(text, finalScale, 0.0F, color, false, Transformation.identity().getMatrix(), buffer, Font.DisplayMode.NORMAL, resultBackgroundOpacity, 15728880);

        buffer.endBatch();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableDepthTest();
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private static boolean hitsAABB(Vec3 eyePosition, Vec3 viewVector, AABB target, double range) {
        Optional<Vec3> clipResult = target.clip(eyePosition, viewVector);
        if (clipResult.isPresent()) {
            Vec3 res = clipResult.get();
            double rangeToRes = eyePosition.distanceToSqr(res);
            return rangeToRes < range || range == 0.0;
        }
        return false;
    }

}
