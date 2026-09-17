package org.warnickwar.sentiencelib.core.debug.components;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.warnickwar.sentiencelib.Constants;
import org.warnickwar.sentiencelib.core.debug.DebugComponent;
import org.warnickwar.sentiencelib.core.debug.DebugComponentType;

public class LevelComponent extends DebugComponent {

    private final ResourceKey<Level> EMPTY_LEVEL = ResourceKey.create(Registries.DIMENSION, Constants.loc("no_level"));

    public ResourceKey<Level> levelId = EMPTY_LEVEL;

    public LevelComponent(DebugComponentType<?> type) {
        super(type);
    }

    public void setLevelId(@NotNull ResourceKey<Level> level) {
        this.levelId = level;
    }

    public boolean isInLevel() {
        return levelId != EMPTY_LEVEL;
    }

    public ResourceKey<Level> getLevelId() {
        return levelId;
    }

    @Override
    public void write(FriendlyByteBuf networkBuffer) {
        networkBuffer.writeResourceKey(levelId);
    }

    @Override
    public void read(FriendlyByteBuf networkBuffer) {
        // Don't particularly like this because it makes a new ResourceKey,
        //  But I'll figure out a way to make it use the cache later.
        levelId = networkBuffer.readResourceKey(Registries.DIMENSION);
    }

}
