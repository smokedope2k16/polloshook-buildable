package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;

public class DrawnCommand extends Command {
   public DrawnCommand() {
      super(new String[]{"Drawn", "hide", "hid"}, new ModuleArgument("mod"));
   }

   public String execute(String[] args) {
      if (args.length == 2) {
         Module module = Managers.getModuleManager().getModuleByAlias(args[1]);
         if (module == null) {
            return "No such module exists";
         } else {
            module.setDrawn(!module.isDrawn());
            String var10000 = module.getLabel();
            return var10000 + " has been " + (module.isDrawn() ? "hidden" : "unhidden");
         }
      } else {
         return this.getInfo();
      }
   }
}
