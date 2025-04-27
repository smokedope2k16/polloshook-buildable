package me.pollos.polloshook.impl.command.modules.configs;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;

public class SaveCommand extends Command {
   public SaveCommand() {
      super(new String[]{"Save", "saveconfig"});
   }

   public String execute(String[] args) {
      Managers.getConfigManager().save();
      return "Saved all configs";
   }
}
