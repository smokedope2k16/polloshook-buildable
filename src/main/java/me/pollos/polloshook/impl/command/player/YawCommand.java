package me.pollos.polloshook.impl.command.player;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;

public class YawCommand extends Command {
   public YawCommand() {
      super(new String[]{"SetYaw", "yaw"}, new Argument("[degree]"));
   }

   public String execute(String[] args) {
      float args1 = Float.parseFloat(args[1]);
      mc.player.setYaw(args1);
      return "Set yaw to %s".formatted(new Object[]{args1});
   }
}
