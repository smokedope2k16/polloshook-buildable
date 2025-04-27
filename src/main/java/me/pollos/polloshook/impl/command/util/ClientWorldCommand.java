package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Command;

public class ClientWorldCommand extends Command {
   public ClientWorldCommand() {
      super(new String[]{"ClientWorld", "isclient"});
   }

   public String execute(String[] args) {
      return "isClient = " + mc.world.isClient;
   }
}
