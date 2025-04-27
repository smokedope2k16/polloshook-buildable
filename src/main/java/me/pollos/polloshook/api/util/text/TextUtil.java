package me.pollos.polloshook.api.util.text;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.asm.ducks.gui.chat.IChatHud;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class TextUtil implements Minecraftable {
   public static final Pattern PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");

   public static void printWithID(Text text, int id) {
      if (mc.inGameHud.getChatHud() != null && mc.player != null) {
         ((IChatHud)mc.inGameHud.getChatHud()).clientMessage(text, id);
      }

   }

   public static String toOrdinal(int i) {
      if (i % 100 >= 11 && i % 100 <= 13) {
         return "th";
      } else {
         String var10000;
         switch(i % 10) {
         case 1:
            var10000 = "st";
            break;
         case 2:
            var10000 = "nd";
            break;
         case 3:
            var10000 = "rd";
            break;
         default:
            var10000 = "th";
         }

         return var10000;
      }
   }

   public static String removeColor(String string) {
      return string != null ? PATTERN.matcher(string).replaceAll("") : "";
   }

   public static String capitalize(String str) {
      String var10000 = str.substring(0, 1).toUpperCase();
      return var10000 + str.substring(1);
   }

   public static String getFixedName(String i) {
      char var10000 = i.charAt(0);
      return var10000 + i.toLowerCase().replaceFirst(Character.toString(i.charAt(0)).toLowerCase(), "");
   }

   public static String concatenate(String[] args, int startIndex) {
      return concatenate(args, startIndex, args.length);
   }

   public static String concatenate(String[] args, int startIndex, int end) {
      if (startIndex >= 0 && startIndex < args.length) {
         if (end > args.length) {
            throw new ArrayIndexOutOfBoundsException(end);
         } else {
            StringBuilder builder = new StringBuilder(args[startIndex]);

            for(int i = startIndex + 1; i < end; ++i) {
               builder.append(" ").append(args[i]);
            }

            return builder.toString();
         }
      } else {
         throw new ArrayIndexOutOfBoundsException(startIndex);
      }
   }

   public static boolean isNullOrEmpty(String str) {
      return str == null || str.isEmpty();
   }

   public static boolean startsWith(String string, String prefix) {
      return string != null && prefix != null ? string.toLowerCase().startsWith(prefix.toLowerCase()) : false;
   }

   public static String translate(OrderedText text) {
      StringBuilder sb = new StringBuilder();
      text.accept((index, style, codePoint) -> {
         sb.appendCodePoint(codePoint);
         return true;
      });
      return sb.toString();
   }

   public static String randomString(int length) {
      String A2Z_09 = "abcdefghijklmnopqrstuvwxyz0123456789";
      StringBuilder sb = new StringBuilder(length);

      for(int i = 0; i < length; ++i) {
         char randomChar = A2Z_09.charAt(ThreadLocalRandom.current().nextInt(A2Z_09.length()));
         if (ThreadLocalRandom.current().nextBoolean()) {
            randomChar = Character.toUpperCase(randomChar);
         }

         sb.append(randomChar);
      }

      return sb.toString();
   }
}
