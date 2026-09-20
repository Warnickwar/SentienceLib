package org.warnickwar.sentiencelib.api.core.debug.components;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.api.core.debug.DebugComponentType;

public class MobNameComponent extends DebugComponent {

    @NotNull
    private String mobName = "";

    public MobNameComponent(DebugComponentType<?> type) {
        super(type);
    }

    public void setMobName(Entity mob) {
        this.mobName = mob.getName().getString();
    }

    @NotNull
    public String getMobName() {
        return mobName;
    }

    @Override
    public void write(ByteBuf networkBuffer) {
        ByteBufCodecs.STRING_UTF8.encode(networkBuffer, this.mobName);
    }

    @Override
    public void read(ByteBuf networkBuffer) {
        mobName = ByteBufCodecs.STRING_UTF8.decode(networkBuffer);
    }

}
