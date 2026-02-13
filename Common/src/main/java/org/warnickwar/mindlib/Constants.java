package org.warnickwar.mindlib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.warnickwar.mindlib.client.renderer.debug.ModDebugRenderers;
import org.warnickwar.mindlib.platform.Services;

public class Constants {

    public static boolean DEBUG = Services.PLATFORM.isDevelopmentEnvironment();

	public static final String MODID = "mindlib";
	public static final String MOD_NAME = "MIND Library";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static class NETWORK {

        public static final ResourceLocation NETWORK_NAME = new ResourceLocation(MODID, "network");
    }

    public static class SERVER {

        @Nullable
        public static MinecraftServer server = null;

    }

    public static class CLIENT {

        @Nullable
        public static ModDebugRenderers DEBUG_RENDERERS = null;
    }
}