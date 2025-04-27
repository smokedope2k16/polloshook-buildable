package me.pollos.polloshook.api.util.text;

import java.util.ArrayList;
import java.util.List;

public record FormattingString(String str, boolean format) {
   public FormattingString(String str, boolean format) {
      this.str = str;
      this.format = format;
   }

   public static List<FormattingString> fromString(String text) {
      List<FormattingString> result = new ArrayList();
      boolean formatted = false;
      String formatStr = "";

      for(int i = 0; i < text.length(); ++i) {
         char ch = text.charAt(i);
         if (ch == 167 && i + 1 < text.length()) {
            formatted = true;
            char var10000 = text.charAt(i + 1);
            formatStr = "§" + var10000;
            ++i;
         } else if (formatted) {
            result.add(new FormattingString(formatStr + ch, true));
         } else {
            result.add(new FormattingString(String.valueOf(ch), false));
         }
      }

      return result;
   }

   public String str() {
      return this.str;
   }

   public boolean format() {
      return this.format;
   }
}
