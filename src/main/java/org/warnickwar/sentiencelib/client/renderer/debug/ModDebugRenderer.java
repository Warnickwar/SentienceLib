package org.warnickwar.sentiencelib.client.renderer.debug;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.client.debug.DebugManagement;
import org.warnickwar.sentiencelib.core.debug.DebugInformation;

import java.util.Collection;
import java.util.Map;

public final class ModDebugRenderer {

    public void render(Minecraft inst, PoseStack poseStack, MultiBufferSource.BufferSource buffer, double camX, double camY, double camZ,
                       Map<DebugSystem, Collection<DebugInformation>> queuedSystems) {
        Collection<DebugInformation> data = DebugManagement.getAllInfo();


    }


}
