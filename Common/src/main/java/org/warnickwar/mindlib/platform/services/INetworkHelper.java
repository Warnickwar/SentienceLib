package org.warnickwar.mindlib.platform.services;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Set;

public interface INetworkHelper {

    <P> void sendToServer(P packet);

    <P> void sendToClients(P packet, Set<ServerPlayer> players);

    <P> void sendToAllClients(P packet);

    <P> void sendToChunk(P packet, LevelChunk chunk);
}
