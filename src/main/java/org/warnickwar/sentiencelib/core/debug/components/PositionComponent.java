package org.warnickwar.sentiencelib.core.debug.components;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;

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
    public void write(FriendlyByteBuf networkBuffer) {
        networkBuffer.writeDouble(position.x);
        networkBuffer.writeDouble(position.y);
        networkBuffer.writeDouble(position.z);
    }

    @Override
    public void read(FriendlyByteBuf networkBuffer) {
        position = new Vec3(
            networkBuffer.readDouble(),
            networkBuffer.readDouble(),
            networkBuffer.readDouble()
        );
    }

}
