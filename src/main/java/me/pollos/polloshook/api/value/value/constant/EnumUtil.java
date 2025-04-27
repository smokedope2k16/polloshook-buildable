package me.pollos.polloshook.api.value.value.constant;



public final class EnumUtil {
   public static Enum<?> fromString(Enum<?> initial, String name) {
      Enum<?> e = fromString(initial.getDeclaringClass(), name);
      return e != null ? e : initial;
   }

   public static <T extends Enum<T>> T fromString(Class<T> type, String name) {
      T[] var2 = type.getEnumConstants();
      int var3 = var2.length;
  
      for (int var4 = 0; var4 < var3; ++var4) {
          T constant = var2[var4];
          if (constant.name().equalsIgnoreCase(name)) {
              return constant;
          }
      }
  
      return null;
  }
  

   public static Enum<?> next(Enum<?> entry) {
      Enum<?>[] array = (Enum[])entry.getDeclaringClass().getEnumConstants();
      return array.length - 1 == entry.ordinal() ? array[0] : array[entry.ordinal() + 1];
   }

   public static Enum<?> previous(Enum<?> entry) {
      Enum<?>[] array = (Enum[])entry.getDeclaringClass().getEnumConstants();
      return entry.ordinal() - 1 < 0 ? array[array.length - 1] : array[entry.ordinal() - 1];
   }

   
   private EnumUtil() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }
}
