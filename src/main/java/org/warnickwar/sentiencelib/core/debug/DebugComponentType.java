package org.warnickwar.sentiencelib.core.debug;

import net.minecraft.resources.ResourceLocation;

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
        // TODO: Make when Registry is made
        return null;
    }

    @Nullable
    public static DebugComponentType<?> getType(ResourceLocation location) {
        return null;
    }

    public static <T extends DebugComponent> DebugComponentType<T> create(ComponentFactory<T> factory) {
        return new DebugComponentType<>(factory);
    }

    @FunctionalInterface
    public interface ComponentFactory<T extends DebugComponent> {
        T create(DebugComponentType<T> type);
    }

}
