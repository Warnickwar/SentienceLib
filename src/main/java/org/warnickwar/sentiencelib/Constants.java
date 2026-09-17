package org.warnickwar.sentiencelib;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

    public static final String MODID = "sentiencelib";
    public static final String MOD_NAME = "Sentience Mob AI Library";
    public static final Logger LOG = LogUtils.getLogger();

    public static boolean DEBUG = !FMLEnvironment.production;

    public static Vec3 DEFAULT_LOCATION = new Vec3(0, 0, 0);

    public static ResourceLocation loc(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public static class NETWORK {

        // Used for Debugging purposes
        public static final ResourceLocation NETWORK_NAME = loc("network");

    }

    public static class SERVER {

        @Nullable
        public static MinecraftServer server = null;

    }

    public static class TEXT_COLOR {
        public static final int WHITE = -1;
        public static final int YELLOW = -256;
        public static final int ORANGE = -23296;
        public static final int GREEN = -16711936;
        public static final int GRAY = -3355444;
        public static final int PINK = -98404;
        public static final int RED = -65536;
    }

    public static class CHAT_COLOR {
        public static final TextColor Enabled = TextColor.parseColor("#55FF55").getOrThrow((str) -> new IllegalStateException("Can't parse Text Color?\n" + str));
        public static final TextColor Disabled = TextColor.parseColor("#FF5555").getOrThrow((str) -> new IllegalStateException("Can't parse Text Color?\n" + str));
    }
}