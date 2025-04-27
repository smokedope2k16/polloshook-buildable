package me.pollos.polloshook.asm.ducks.util;

import net.minecraft.network.packet.Packet;

public interface IClientConnection {
   Packet<?> sendPacketNoEvent(Packet<?> var1);
}
