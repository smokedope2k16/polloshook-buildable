package me.pollos.polloshook.impl.module.other.font.util;

import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.font.CustomFont;

public class FontArgument extends Argument {
   public FontArgument(String label) {
      super(label);
   }

   public String predict(String currentArg) {
      CustomFont CUSTOM_FONT = (CustomFont)Managers.getModuleManager().get(CustomFont.class);
      Iterator var3 = CUSTOM_FONT.getFonts().iterator();

      String str;
      do {
         if (!var3.hasNext()) {
            return super.predict(currentArg);
         }

         str = (String)var3.next();
      } while(!str.toLowerCase().startsWith(currentArg.toLowerCase()));

      return str.replace(" ", "_");
   }
}
