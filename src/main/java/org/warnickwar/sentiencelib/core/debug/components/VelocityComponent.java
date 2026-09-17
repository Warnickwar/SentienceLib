package org.warnickwar.sentiencelib.core.debug.components;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;

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
    public void write(FriendlyByteBuf networkBuffer) {
        networkBuffer.writeDouble(momentum.x);
        networkBuffer.writeDouble(momentum.y);
        networkBuffer.writeDouble(momentum.z);
    }

    @Override
    public void read(FriendlyByteBuf networkBuffer) {
        momentum = new Vec3(
            networkBuffer.readDouble(),
            networkBuffer.readDouble(),
            networkBuffer.readDouble()
        );
    }

}
