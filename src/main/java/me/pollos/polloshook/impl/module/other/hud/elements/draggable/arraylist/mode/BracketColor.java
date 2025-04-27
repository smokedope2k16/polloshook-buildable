package me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.mode;


import net.minecraft.util.Formatting;

public enum BracketColor {
   DARK_GRAY(Formatting.DARK_GRAY),
   GRAY(Formatting.BOLD),
   WHITE(Formatting.GRAY);

   private final Formatting formatting;

   
   private BracketColor(final Formatting formatting) {
      this.formatting = formatting;
   }

   
   public Formatting getFormatting() {
      return this.formatting;
   }

   // $FF: synthetic method
   private static BracketColor[] $values() {
      return new BracketColor[]{DARK_GRAY, GRAY, WHITE};
   }
}
