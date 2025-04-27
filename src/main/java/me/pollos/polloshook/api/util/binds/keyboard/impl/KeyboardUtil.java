package me.pollos.polloshook.api.util.binds.keyboard.impl;

import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public class KeyboardUtil {
   public static List<Integer> REMOVE_BINDS_LIST = Arrays.asList(256, 32, 261, 259);

   public static int getKeyNumberFromName(String name) {
      return InputUtil.fromTranslationKey(name).getCode();
   }

   public static String getKeyNameFromNumber(int number) {
      return getKeyNameFromNumber(number, false);
   }

   public static String getKeyNameFromNumber(int number, boolean spaced) {
      if (number >= 1 && number <= 4) {
         switch(number) {
         case 1:
            return "Right Click".toUpperCase();
         case 2:
            return "Middle Click".toUpperCase();
         case 3:
            return "Thumb 3".toUpperCase();
         case 4:
            return "Thumb 4".toUpperCase();
         }
      }

      return InputUtil.fromKeyCode(number, 0).getTranslationKey().replace("key.keyboard.", "").replace(".", spaced ? " " : "");
   }

   public static KeyPressAction getActionByInt(int i) {
      switch(i) {
      case 0:
         return KeyPressAction.RELEASE;
      case 1:
         return KeyPressAction.PRESS;
      case 2:
         return KeyPressAction.REPEAT;
      default:
         return null;
      }
   }

   public static boolean isCTRL() {
      return isPressed(341);
   }

   public static boolean isShift() {
      return isPressed(340);
   }

   public static boolean isCopying() {
      return isPressed(341) && isPressed(67);
   }

   public static boolean isPasting() {
      return isPressed(341) && isPressed(86);
   }

   public static boolean isPressed(int keyCode) {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode);
   }
}
