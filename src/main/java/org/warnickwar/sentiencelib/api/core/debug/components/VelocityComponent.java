package org.warnickwar.sentiencelib.api.core.debug.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

public class VelocityComponent extends DebugComponent {

    private Vec3 momentum = Constants.DEFAULT_LOCATION;

    public VelocityComponent(@NotNull DebugComponentType<?> type) {
        super(type);
    }

    public void setMomentum(@NotNull Vec3 momentum) {
        this.momentum = momentum;
    }

    public Vec3 getMomentum() {
        return  momentum;
    }

    @Override
    public void write(ByteBuf networkBuffer) {
        networkBuffer.writeDouble(momentum.x);
        networkBuffer.writeDouble(momentum.y);
        networkBuffer.writeDouble(momentum.z);
    }

    @Override
    public void read(ByteBuf networkBuffer) {
        momentum = new Vec3(
            networkBuffer.readDouble(),
            networkBuffer.readDouble(),
            networkBuffer.readDouble()
        );
    }

}
