package me.pollos.polloshook.impl.command.modules;

import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;

public class ResetCommand extends Command {
   public ResetCommand() {
      super(new String[]{"Reset", "rset", "resetsetting", "resetvalue"}, new ModuleArgument("[module]"));
   }

   public String execute(String[] args) {
      String args1 = args[1];
      Module module = Managers.getModuleManager().getModuleByAlias(args1);
      if (module == null) {
         return "Module does not exist";
      } else if (args.length == 2) {
         module.getPresetByLabel("default").execute();
         return "Reset module %s".formatted(new Object[]{module.getLabel()});
      } else if (!module.getValues().isEmpty()) {
         String args2 = args[2];
         Value<?> value = module.getValueByLabel(args2);
         if (value == null) {
            return "No value with label %s".formatted(new Object[]{args2});
         } else {
            value.resetToDefaultValue();
            return "Reset value %s to default setting%s".formatted(new Object[]{value.getLabel(), !(value instanceof ColorValue) && !(value instanceof BlockListValue) && !(value instanceof ItemListValue) && !(value instanceof TargetValue) ? "" : "s"});
         }
      } else {
         return "Module has no values";
      }
   }
}
