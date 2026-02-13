package org.warnickwar.mindlib.platform;

import org.warnickwar.mindlib.Constants;
import org.warnickwar.mindlib.platform.services.INetworkHelper;
import org.warnickwar.mindlib.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static final INetworkHelper HELPER = load(INetworkHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
