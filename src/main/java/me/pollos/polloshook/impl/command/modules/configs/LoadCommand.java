package me.pollos.polloshook.impl.command.modules.configs;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;

public class LoadCommand extends Command {
   public LoadCommand() {
      super(new String[]{"Load", "loadconfig"});
   }

   public String execute(String[] args) {
      Managers.getConfigManager().load();
      return "Loaded all configs";
   }
}
