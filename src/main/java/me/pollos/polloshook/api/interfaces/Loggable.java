package me.pollos.polloshook.api.interfaces;

import net.minecraft.text.Text;

public interface Loggable {
   void log(String var1);

   void log(String var1, int var2);

   void log(String var1, boolean var2);

   void log(Text var1);

   void log(Text var1, int var2);

   void log(Text var1, boolean var2);

   default void log(Number text, boolean delete) {
      this.log(text.toString(), delete);
   }

   default void log(Boolean text, boolean delete) {
      this.log(text.toString(), delete);
   }

   default void log(Enum<?> text, boolean delete) {
      this.log(text.toString(), delete);
   }

   void logNoMark(String var1);

   void logNoMark(String var1, int var2);

   void report(String var1, Exception var2);

   void info(String var1);

   default void info(Number text) {
      this.info(text.toString());
   }

   default void info(Boolean text) {
      this.info(text.toString());
   }

   default void info(Enum<?> text) {
      this.info(text.toString());
   }

   void error(String var1);

   void warn(String var1);
}
