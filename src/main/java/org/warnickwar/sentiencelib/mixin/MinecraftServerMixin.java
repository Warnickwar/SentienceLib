package org.warnickwar.sentiencelib.mixin;

import org.warnickwar.sentiencelib.Constants;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.warnickwar.sentiencelib.threading.JobManager;

import java.net.Proxy;

// Has to be on the common side due to
//  integrated servers existing.
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow
    public abstract boolean isRunning();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void sentience$onServerStart(Thread p_236723_, LevelStorageSource.LevelStorageAccess p_236724_, PackRepository p_236725_, WorldStem p_236726_, Proxy p_236727_, DataFixer p_236728_, Services p_236729_, ChunkProgressListenerFactory p_236730_, CallbackInfo ci) {
        Constants.SERVER.server = (MinecraftServer) (Object) this;
        JobManager.open();
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    public void sentience$onServerClose(CallbackInfo ci) {
        if (isRunning()) {
            Constants.SERVER.server = null;
            JobManager.close();
        }
    }
}
