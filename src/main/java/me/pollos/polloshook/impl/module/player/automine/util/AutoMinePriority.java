package me.pollos.polloshook.impl.module.player.automine.util;

public enum AutoMinePriority {
   SELF(8),
   BURROW(7),
   SURROUND_AIR(6),
   CRYSTAL_HEAD(5),
   CRYSTAL_HEAD_BLOCK(4),
   CRYSTAL(3),
   SURROUND(2),
   ENDER_CHEST(1);

   private final int value;

   private AutoMinePriority(int priority) {
      this.value = priority;
   }

   public int getValue() {
      return this.value;
   }

   // $FF: synthetic method
   private static AutoMinePriority[] $values() {
      return new AutoMinePriority[]{SELF, BURROW, SURROUND_AIR, CRYSTAL_HEAD, CRYSTAL_HEAD_BLOCK, CRYSTAL, SURROUND, ENDER_CHEST};
   }
}
