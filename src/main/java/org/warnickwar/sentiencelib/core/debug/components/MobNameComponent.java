package org.warnickwar.sentiencelib.core.debug.components;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;

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
    public void write(FriendlyByteBuf networkBuffer) {
        networkBuffer.writeUtf(mobName);
    }

    @Override
    public void read(FriendlyByteBuf networkBuffer) {
        networkBuffer.readUtf();
    }

}
