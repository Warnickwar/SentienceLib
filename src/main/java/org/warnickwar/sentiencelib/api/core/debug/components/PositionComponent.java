package org.warnickwar.sentiencelib.api.core.debug.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.phys.Vec3;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

public class PositionComponent extends DebugComponent {

    private Vec3 position = Constants.DEFAULT_LOCATION;

    public PositionComponent(DebugComponentType<?> type) {
        super(type);
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getPosition() {
        return this.position;
    }

    @Override
    public void write(ByteBuf networkBuffer) {
        networkBuffer.writeDouble(position.x);
        networkBuffer.writeDouble(position.y);
        networkBuffer.writeDouble(position.z);
    }

    @Override
    public void read(ByteBuf networkBuffer) {
        position = new Vec3(
            networkBuffer.readDouble(),
            networkBuffer.readDouble(),
            networkBuffer.readDouble()
        );
    }

}
