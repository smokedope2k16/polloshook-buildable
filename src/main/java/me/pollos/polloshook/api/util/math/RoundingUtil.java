package me.pollos.polloshook.api.util.math;

public class RoundingUtil {
   public static int roundToStep(int value, int step) {
      return Math.round((float)value / (float)step) * step;
   }

   public static long roundToStep(long value, long step) {
      return Math.round((double)value / (double)step) * step;
   }

   public static float roundToStep(float value, float step) {
      return (float)Math.round(value / step) * step;
   }

   public static double roundToStep(double value, double step) {
      return (double)Math.round(value / step) * step;
   }
}
