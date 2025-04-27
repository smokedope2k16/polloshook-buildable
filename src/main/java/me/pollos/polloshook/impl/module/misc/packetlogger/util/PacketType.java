package me.pollos.polloshook.impl.module.misc.packetlogger.util;

public enum PacketType {
   CLIENT,
   SERVER,
   BOTH;

   // $FF: synthetic method
   private static PacketType[] $values() {
      return new PacketType[]{CLIENT, SERVER, BOTH};
   }
}
