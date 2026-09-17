package org.warnickwar.sentiencelib.core.debug;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public abstract class DebugComponent {

    private DebugComponentType<?> type;

    public DebugComponent(@NotNull DebugComponentType<?> type) {
        this.type = type;
    }

    public DebugComponentType<?> getType() {
        return this.type;
    }

    public abstract void write(FriendlyByteBuf networkBuffer);

    public abstract void read(FriendlyByteBuf networkBuffer);
}
