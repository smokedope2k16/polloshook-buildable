package me.pollos.polloshook.impl.command.util;

import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.font.CustomFont;

public class FontsCommand extends Command {
   public FontsCommand() {
      super(new String[]{"Fonts", "fontlist"});
   }

   public String execute(String[] args) {
      StringJoiner sj = new StringJoiner(", ");
      List<String> fonts = ((CustomFont)Managers.getModuleManager().get(CustomFont.class)).getFonts();
      Iterator var4 = fonts.iterator();

      while(var4.hasNext()) {
         String font = (String)var4.next();
         sj.add(font);
      }

      return fonts.isEmpty() ? "No fonts found..." : sj.toString();
   }
}
