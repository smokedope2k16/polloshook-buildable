package me.pollos.polloshook.api.util.discordrpc;

public enum Opcode {
   Handshake,
   Frame,
   Close,
   Ping,
   Pong;

   private static final Opcode[] VALUES = values();

   public static Opcode valueOf(int i) {
      return VALUES[i];
   }

   // $FF: synthetic method
   private static Opcode[] $values() {
      return new Opcode[]{Handshake, Frame, Close, Ping, Pong};
   }
}
