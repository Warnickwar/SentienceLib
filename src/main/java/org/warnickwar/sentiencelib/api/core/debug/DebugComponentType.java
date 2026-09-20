package org.warnickwar.sentiencelib.api.core.debug;

import net.minecraft.resources.ResourceLocation;
import org.warnickwar.sentiencelib.api.ModRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class DebugComponentType<T extends DebugComponent> {

    private final ComponentFactory<T> componentFactory;

    DebugComponentType(ComponentFactory<T> componentFactory) {
        this.componentFactory = componentFactory;
    }

    public T construct(Consumer<T> setup) {
        T res = componentFactory.create(this);
        setup.accept(res);
        return res;
    }

    public T constructDefault() {
        return componentFactory.create(this);
    }

    @Nullable
    public static ResourceLocation getKey(DebugComponentType<?> component) {
        return ModRegistries.DEBUG_COMPONENTS.getKey(component);
    }

    @Nullable
    public static DebugComponentType<?> getType(ResourceLocation location) {
        return ModRegistries.DEBUG_COMPONENTS.get(location);
    }

    public static <T extends DebugComponent> DebugComponentType<T> create(ComponentFactory<T> factory) {
        return new DebugComponentType<>(factory);
    }

    @FunctionalInterface
    public interface ComponentFactory<T extends DebugComponent> {
        T create(DebugComponentType<T> type);
    }

}
