package me.pollos.polloshook.impl.module.movement.phase.util;

public enum OffsetType {
   FULL(0.0624D),
   SOFT(0.059D),
   SEMI(0.03D),
   PROTOCOL(1.0E-8D);

   private final double depth;

   private OffsetType(double depth) {
      this.depth = depth;
   }

   public double getDepth() {
      return this.depth;
   }

   // $FF: synthetic method
   private static OffsetType[] $values() {
      return new OffsetType[]{FULL, SOFT, SEMI, PROTOCOL};
   }
}
