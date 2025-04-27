package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Command;

public class ReloadWorldCommand extends Command {
   public ReloadWorldCommand() {
      super(new String[]{"ReloadWorld", "reload"});
   }

   public String execute(String[] args) {
      mc.worldRenderer.reload();
      return "Reloading...";
   }
}
