package me.pollos.polloshook.api.util.math;

import java.math.BigDecimal;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
   public static int clamp(int num, int min, int max) {
      return num < min ? min : Math.min(num, max);
   }

   public static float clamp(float num, float min, float max) {
      return num < min ? min : Math.min(num, max);
   }

   public static double clamp(double num, double min, double max) {
      return num < min ? min : Math.min(num, max);
   }

   public static boolean isBetween(float number, int min, int max) {
      return number > (float)min && number < (float)max;
   }

   public static boolean isBetween(long number, int min, int max) {
      return number > (long)min && number < (long)max;
   }

   public static double round(double value, int places) {
      return places < 0 ? value : (new BigDecimal(value)).setScale(places, 5).doubleValue();
   }

   public static Object round(String value, int places) {
      return places < 0 ? value : (new BigDecimal(value)).setScale(places, 5).floatValue();
   }

   public static int intFromString(String str) {
      return Integer.parseInt(str);
   }

   public static float square(float in) {
      return in * in;
   }

   public static float rad(float angle) {
      return (float)((double)angle * 3.141592653589793D / 180.0D);
   }

   public static double angle(Vec3d vec3d, Vec3d other) {
      double lengthSq = vec3d.length() * other.length();
      if (lengthSq < 1.0E-4D) {
         return 0.0D;
      } else {
         double dot = vec3d.dotProduct(other);
         double arg = dot / lengthSq;
         if (arg > 1.0D) {
            return 0.0D;
         } else {
            return arg < -1.0D ? 180.0D : Math.acos(arg) * 180.0D / 3.141592653589793D;
         }
      }
   }

   public static double distance2D(Vec3d from, Vec3d to) {
      double x = to.x - from.x;
      double z = to.z - from.z;
      return Math.sqrt(x * x + z * z);
   }

   public static float fixedNametagScaling(float scale) {
      return scale * 0.01F;
   }

   public static Vec3d getVecRelativeToPlayer(double x, double y, double z, Vec3d playerVec, double maxXZ, double maxY) {
      double playerX = playerVec.x;
      double playerY = playerVec.y;
      double playerZ = playerVec.z;
      double differenceX = 0.0D;
      boolean addX = false;
      if (x < playerX) {
         addX = true;
         differenceX = playerX - x;
      } else if (x > playerX) {
         differenceX = x - playerX;
      }

      double rX = Math.min(differenceX, addX ? maxXZ : -maxXZ);
      double differenceZ = 0.0D;
      boolean addZ = false;
      if (z < playerZ) {
         addZ = true;
         differenceZ = playerZ - z;
      } else if (z > playerX) {
         differenceZ = z - playerZ;
      }

      double rZ = Math.min(differenceZ, addZ ? maxXZ : -maxXZ);
      double rY;
      if (y < playerY) {
         double differenceY = playerY - y;
         rY = Math.min(differenceY, maxY);
      } else {
         rY = 0.0D;
      }

      return new Vec3d(x + rX, y + rY, z + rZ);
   }
}
