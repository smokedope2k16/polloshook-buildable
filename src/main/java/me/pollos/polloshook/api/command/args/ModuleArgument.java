package me.pollos.polloshook.api.command.args;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.util.CommandUtil;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;

public class ModuleArgument extends Argument {
   public ModuleArgument(String label) {
      super(label);
   }

   public String predict(String currentArg) {
      Module module = (Module)CommandUtil.getLabeledStartingWith(currentArg, Managers.getModuleManager().getModules());
      return module != null ? module.getLabel() : currentArg;
   }
}
