package org.warnickwar.sentience.client.renderer.debug;

import org.warnickwar.sentience.Constants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

public class ModDebugRenderers {

    public final EntityAgentDebug MIND_DEBUG;

    private final Minecraft minecraft;

    public ModDebugRenderers(Minecraft mc) {
        this.minecraft = mc;
        this.MIND_DEBUG = new EntityAgentDebug(this.minecraft);
    }

    public void render(PoseStack poseStack, MultiBufferSource.BufferSource buffer, double camX, double camY, double camZ) {
        if (!Constants.DEBUG) return;
        MIND_DEBUG.render(poseStack, buffer, camX, camY, camZ);
    }

    public void clear() {
        MIND_DEBUG.clear();
    }
}
