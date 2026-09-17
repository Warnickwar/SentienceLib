package org.warnickwar.sentiencelib.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.warnickwar.sentiencelib.client.debug.DebugManagement;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    private ProfilerFiller profiler;

    @Shadow
    public abstract DeltaTracker getTimer();

    @Inject(method = "tick", at = @At("HEAD"))
    void sentience$preClientTick(CallbackInfo ci) {
        profiler.push("sentience$clientDebugUpdate");
        // TODO: Fix DeltaTime
        DebugManagement.update(getTimer().getRealtimeDeltaTicks());
        profiler.pop();
    }

}
