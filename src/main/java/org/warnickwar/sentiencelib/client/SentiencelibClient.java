package org.warnickwar.sentiencelib.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.client.debug.RegisterDebugSystemEvent;
import org.warnickwar.sentiencelib.client.events.ClientLifecycleSubscribers;
import org.warnickwar.sentiencelib.client.events.ClientSubscribers;

/**
 * If you are looking for the SentienceLib Common Class, see {@link org.warnickwar.sentiencelib.Sentiencelib}
 * @see org.warnickwar.sentiencelib.Sentiencelib
 */
@Mod(value = Constants.MODID, dist = Dist.CLIENT)
public class SentiencelibClient {

    public SentiencelibClient(IEventBus modEventBus) {
        // Register Client-only events
        modEventBus.register(ClientLifecycleSubscribers.class);
        NeoForge.EVENT_BUS.register(ClientSubscribers.class);

        modEventBus.post(new RegisterDebugSystemEvent());


    }
}
