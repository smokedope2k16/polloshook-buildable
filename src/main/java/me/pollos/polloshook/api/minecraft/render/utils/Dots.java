package me.pollos.polloshook.api.minecraft.render.utils;

import me.pollos.polloshook.api.util.math.StopWatch;

public final class Dots {
   private static String dots = "";
   private static boolean up = true;
   private static final StopWatch dotTimer = new StopWatch();

   private Dots() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static String get3Dots() {
      if (dotTimer.passed(350L)) {
         if (up) {
            dots += ".";
         } else if (!dots.isEmpty()) {
            dots = dots.substring(0, dots.length() - 1);
         }

         if (dots.length() == 3) {
            up = false;
         } else if (dots.isEmpty()) {
            up = true;
         }

         dotTimer.reset();
      }
      return dots;
   }
}