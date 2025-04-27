package me.pollos.polloshook.impl.module.player.blink.mode;

public enum BlinkMode {
   PULSE,
   FAKE_LAG,
   CONSTANT;

   // $FF: synthetic method
   private static BlinkMode[] $values() {
      return new BlinkMode[]{PULSE, FAKE_LAG, CONSTANT};
   }
}
