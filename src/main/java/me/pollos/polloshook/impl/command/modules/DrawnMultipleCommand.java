package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;

public class DrawnMultipleCommand extends Command {
   public DrawnMultipleCommand() {
      super(new String[]{"DrawnMany", "drawnmultiple"}, new ModuleArgument("[modules]"));
   }

   public String execute(String[] args) {
      if (args.length < 2) {
         return "No module entered";
      } else {
         String[] array = args[1].split(",");
         int count = 0;
         String drawnModuleLabel = "hi";
         String[] var5 = array;
         int var6 = array.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String str = var5[var7];
            str = str.trim();
            Module module = Managers.getModuleManager().getModuleByAlias(str);
            if (module != null) {
               drawnModuleLabel = module.getLabel();
               module.setDrawn(!module.isDrawn());
               ++count;
            }
         }

         return count > 1 ? "Drawn %s modules".formatted(new Object[]{count}) : "Drawn module [%s]".formatted(new Object[]{drawnModuleLabel});
      }
   }
}
