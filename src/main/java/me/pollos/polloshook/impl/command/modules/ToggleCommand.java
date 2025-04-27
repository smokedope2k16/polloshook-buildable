package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;

public class ToggleCommand extends Command {
   public ToggleCommand() {
      super(new String[]{"Toggle", "t"}, new ModuleArgument("[mod]"));
   }

   public String execute(String[] args) {
      Module module = Managers.getModuleManager().getModuleByAlias(args[1]);
      if (module == null) {
         return "No such module exists";
      } else if (module instanceof ToggleableModule) {
         ToggleableModule toggleable = (ToggleableModule)module;
         toggleable.toggle();
         return "%s has been toggled %s".formatted(new Object[]{module.getLabel(), toggleable.isEnabled() ? "on" : "off"});
      } else {
         return "That module is not toggleable";
      }
   }
}
