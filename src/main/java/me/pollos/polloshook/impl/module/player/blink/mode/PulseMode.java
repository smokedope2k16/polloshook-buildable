package me.pollos.polloshook.impl.module.player.blink.mode;

public enum PulseMode {
   PACKETS,
   DISTANCE,
   TIME;

   // $FF: synthetic method
   private static PulseMode[] $values() {
      return new PulseMode[]{PACKETS, DISTANCE, TIME};
   }
}
