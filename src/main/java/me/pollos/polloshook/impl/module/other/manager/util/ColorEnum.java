package me.pollos.polloshook.impl.module.other.manager.util;

import net.minecraft.util.Formatting;

public enum ColorEnum {
   BLACK(Formatting.BLACK),
   WHITE(Formatting.GRAY),
   DARK_BLUE(Formatting.DARK_BLUE),
   DARK_GREEN(Formatting.DARK_GREEN),
   DARK_AQUA(Formatting.DARK_AQUA),
   DARK_RED(Formatting.DARK_RED),
   DARK_PURPLE(Formatting.DARK_PURPLE),
   DARK_GRAY(Formatting.DARK_GRAY),
   GRAY(Formatting.BOLD),
   GOLD(Formatting.GOLD),
   BLUE(Formatting.BLUE),
   GREEN(Formatting.GREEN),
   AQUA(Formatting.AQUA),
   RED(Formatting.RED),
   LIGHT_PURPLE(Formatting.LIGHT_PURPLE),
   YELLOW(Formatting.WHITE);

   private final Formatting color;

   private ColorEnum(Formatting color) {
      this.color = color;
   }

   public Formatting getColor() {
      return this.color;
   }

   // $FF: synthetic method
   private static ColorEnum[] $values() {
      return new ColorEnum[]{BLACK, WHITE, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, DARK_GRAY, GRAY, GOLD, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW};
   }
}