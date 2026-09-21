package org.warnickwar.sentiencelib.api.client.debug.renderer.system;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.client.debug.renderer.DebugSystem;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;
import org.warnickwar.sentiencelib.api.core.debug.DebugInformation;
import org.warnickwar.sentiencelib.api.core.debug.components.*;
import org.warnickwar.sentiencelib.api.core.debug.ModDebugComponents;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BasicEntityDebugRenderer extends DebugSystem {

    private static final double RENDER_RANGE = 30.0D;

    private static final float TYPE_TEXT_SCALE = 0.025F;
    private static final float NAME_TEXT_SCALE = 0.025F;
    private static final float ACTION_TEXT_SCALE = 0.025F;
    private static final float DESIRE_TEXT_SCALE = 0.0275F;

    private static final float STARTING_OFFSET = 1.25F;
    private static final float OFFSET_MOD = 0.25F;

    private static final double RAYCAST_DISTANCE = 8;

    private UUID lastLookedAtUuid;

    @Override
    public void preRender(Collection<DebugInformation> validInformationComponents) {

        Entity camEntity = Minecraft.getInstance().cameraEntity;
        assert camEntity != null;
        Vec3 eyePosition = camEntity.getEyePosition();
        Vec3 viewVector = camEntity.getViewVector(1.0F).scale(RAYCAST_DISTANCE);

        for (DebugInformation info : validInformationComponents) {
            AABB infoAABB = AABB.ofSize(info.getComponentSafe(ModDebugComponents.POSITION).getVector(), 1.0, 1.0, 1.0);
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
        Vec3Component pos = current.getComponentSafe(ModDebugComponents.POSITION);

        // Consider Velocity if supplied
        Vec3Component velocity = current.getComponent(ModDebugComponents.VELOCITY);

        // get Partial Tick
        float partialTick = getCurrentPartialTick(client);

        Vec3 resPos = pos.getVector();
        if (velocity != null) {
            Vec3 velocityVector = velocity.getVector();
            resPos = resPos.add(velocityVector.x * partialTick, velocityVector.y * partialTick, velocityVector.z * partialTick);
        }

        if (resPos.distanceToSqr(camX, camY, camZ) >= RENDER_RANGE * RENDER_RANGE) {
            return;
        }

        // Render normally according to specifications
        boolean isSelected = lastLookedAtUuid == current.getUUID();
        // Done to properly render the stack
        float offset = STARTING_OFFSET;

        EntityTypeComponent type = current.getComponentSafe(ModDebugComponents.MOB_TYPE);
        BasicDetailsComponent mindDetails = current.getComponentSafe(ModDebugComponents.BASIC_DETAILS);

        MobNameComponent name = current.getComponent(ModDebugComponents.MOB_NAME);

        Quaternionf orientation = client.gameRenderer.getMainCamera().rotation();

        EntityType<?> entityType = type.getEntityType();
        assert entityType != null;

        Font font = client.font;

        renderFloatingText(font, camX, camY, camZ, orientation,
            poseStack, buffer, EntityType.getKey(entityType).toString(),
            resPos.x, resPos.y + offset, resPos.z,
            Constants.TEXT_COLOR.WHITE, TYPE_TEXT_SCALE, 0);
        offset += OFFSET_MOD;

        // Render a custom name as well if the Entity has one
        if (name != null) {
            renderFloatingText(font, camX, camY, camZ, orientation,
                poseStack, buffer, name.getMobName(),
                resPos.x, resPos.y + offset, resPos.z,
                Constants.TEXT_COLOR.WHITE, NAME_TEXT_SCALE, 0);
            offset += OFFSET_MOD;
        }

        // Display Actions if Selected
        if (isSelected) {
            String[] currentActions = mindDetails.getCurrentActions();
            for (int i = 4; i >= 0; i--) {
                int color = i == 0 ?
                    Constants.TEXT_COLOR.WHITE :
                    Constants.TEXT_COLOR.ORANGE;
                renderFloatingText(font, camX, camY, camZ, orientation,
                    poseStack, buffer, (i+1) + ": " + currentActions[i],
                    resPos.x, resPos.y + offset, resPos.z,
                    color, ACTION_TEXT_SCALE, 0);
                offset += OFFSET_MOD;
            }
        }

        // Always render current Desire
        renderFloatingText(font, camX, camY, camZ, orientation,
            poseStack, buffer, mindDetails.getCurrentDesire(),
            resPos.x, resPos.y + offset, resPos.z,
            mindDetails.hasDesire() ?
            Constants.TEXT_COLOR.RED : Constants.TEXT_COLOR.GREEN,
            DESIRE_TEXT_SCALE, 0);
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

    public static void renderFloatingText(Font font, double camX, double camY, double camZ, Quaternionf cameraRotation,
                                          PoseStack poseStack, MultiBufferSource bufferSource,
                                          String text,
                                          double x, double y, double z,
                                          int color, float scale,
                                          float xOffset) {
            poseStack.pushPose();
            poseStack.translate((float)(x - camX), (float)(y - camY), (float)(z - camZ));
            poseStack.mulPose(cameraRotation);
            poseStack.scale(scale, -scale, scale);
            float f = (float)(-font.width(text)) / 2.0F;
            f -= xOffset / scale;
            font.drawInBatch(text, f, 0.0F, color, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, 15728880);
            poseStack.popPose();

    }

    @SuppressWarnings("SameParameterValue")
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
