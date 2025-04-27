package me.pollos.polloshook.api.util.math;

import java.util.Random;

public class RandomUtil {
   private static final Random rnd = new Random();
   private static final String A2Z_09 = "abcdefghijklmnopqrstuvwxyz0123456789";

   public static boolean passedChance(int chance) {
      if (chance == 100) {
         return true;
      } else if (chance == 0) {
         return false;
      } else {
         int randomNumber = rnd.nextInt(100) + 1;
         return randomNumber <= chance;
      }
   }

   public static String newRandomString(int length) {
      StringBuilder sb = new StringBuilder(length);

      for(int i = 0; i < length; ++i) {
         char randomChar = "abcdefghijklmnopqrstuvwxyz0123456789".charAt(getRandom().nextInt("abcdefghijklmnopqrstuvwxyz0123456789".length()));
         if (getRandom().nextBoolean()) {
            randomChar = Character.toUpperCase(randomChar);
         }

         sb.append(randomChar);
      }

      return sb.toString();
   }

   public static Random getRandom() {
      return rnd;
   }
}
