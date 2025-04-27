package me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.util;

import java.util.TreeMap;

public class RomanNumber {
   private static final TreeMap<Integer, String> INTEGER_STRING_TREE_MAP = new TreeMap();

   public static String toRoman(int number) {
      try {
         if (number <= 0) {
            return Integer.toString(number);
         }
         int l = INTEGER_STRING_TREE_MAP.floorKey(number);
         if (number == l) {
            return INTEGER_STRING_TREE_MAP.get(number);
         } else {
            return INTEGER_STRING_TREE_MAP.get(l) + toRoman(number - l);
         }
      } catch (Exception var2) {
         return Integer.toString(number);
      }
   }

   static {
      INTEGER_STRING_TREE_MAP.put(1000, "M");
      INTEGER_STRING_TREE_MAP.put(900, "CM");
      INTEGER_STRING_TREE_MAP.put(500, "D");
      INTEGER_STRING_TREE_MAP.put(400, "CD");
      INTEGER_STRING_TREE_MAP.put(100, "C");
      INTEGER_STRING_TREE_MAP.put(90, "XC");
      INTEGER_STRING_TREE_MAP.put(50, "L");
      INTEGER_STRING_TREE_MAP.put(40, "XL");
      INTEGER_STRING_TREE_MAP.put(10, "X");
      INTEGER_STRING_TREE_MAP.put(9, "IX");
      INTEGER_STRING_TREE_MAP.put(5, "V");
      INTEGER_STRING_TREE_MAP.put(4, "IV");
      INTEGER_STRING_TREE_MAP.put(1, "I");
   }
}