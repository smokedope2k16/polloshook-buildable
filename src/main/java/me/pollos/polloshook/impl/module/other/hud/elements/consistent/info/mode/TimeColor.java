package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.mode;


import net.minecraft.util.Formatting;

public enum TimeColor {
   WHITE(Formatting.GRAY),
   GRAY(Formatting.BOLD),
   NONE(Formatting.RESET);

   final Formatting format;

   
   private TimeColor(final Formatting format) {
      this.format = format;
   }

   
   public Formatting getFormat() {
      return this.format;
   }

   // $FF: synthetic method
   private static TimeColor[] $values() {
      return new TimeColor[]{WHITE, GRAY, NONE};
   }
}