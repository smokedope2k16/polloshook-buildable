package me.pollos.polloshook.impl.module.other.font.util;



public enum FontStyle {
   PLAIN(0),
   BOLD(1),
   ITALIC(2),
   ALL(3);

   private final int fontStyle;

   
   public int getFontStyle() {
      return this.fontStyle;
   }

   
   private FontStyle(final int fontStyle) {
      this.fontStyle = fontStyle;
   }

   // $FF: synthetic method
   private static FontStyle[] $values() {
      return new FontStyle[]{PLAIN, BOLD, ITALIC, ALL};
   }
}
