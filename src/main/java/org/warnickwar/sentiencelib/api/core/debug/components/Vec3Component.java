package org.warnickwar.sentiencelib.api.core.debug.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

@SuppressWarnings("unused")
public class Vec3Component extends DebugComponent {

    private Vec3 vector = Constants.DEFAULT_LOCATION;

    public Vec3Component(@NotNull DebugComponentType<?> type) {
        super(type);
    }

    public void setVector(Vec3 vector) {
        this.vector = vector;
    }

    public Vec3 getVector() {
        return this.vector;
    }

    @Override
    public void write(ByteBuf networkBuffer) {
        networkBuffer.writeDouble(vector.x);
        networkBuffer.writeDouble(vector.y);
        networkBuffer.writeDouble(vector.z);
    }

    @Override
    public void read(ByteBuf networkBuffer) {
        vector = new Vec3(
            networkBuffer.readDouble(),
            networkBuffer.readDouble(),
            networkBuffer.readDouble()
        );
    }

}
