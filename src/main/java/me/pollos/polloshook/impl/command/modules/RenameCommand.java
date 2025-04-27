package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.text.TextUtil;

public class RenameCommand extends Command {
   public RenameCommand() {
      super(new String[]{"Rename", "setlabel"}, new ModuleArgument("[mod]"), new Argument("[label]"));
   }

   public String execute(String[] args) {
      Module module = Managers.getModuleManager().getModuleByAlias(args[1]);
      if (module == null) {
         return "No such module exists";
      } else if (args[2] == null) {
         return "type sum text bitch ass nigga";
      } else {
         module.setDisplayLabel(TextUtil.concatenate(args, 2));
         return "Renamed %s to %s".formatted(new Object[]{module.getLabel(), TextUtil.concatenate(args, 2)});
      }
   }
}
