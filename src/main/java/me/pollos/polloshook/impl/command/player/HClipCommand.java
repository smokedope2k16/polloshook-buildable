package me.pollos.polloshook.impl.command.player;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;

public class HClipCommand extends Command {
   public HClipCommand() {
      super(new String[]{"HClip", "clip", "hc"}, new Argument("[number]"));
   }

   public String execute(String[] args) {
      float distance = Float.parseFloat(args[1]);
      double yaw = Math.cos(Math.toRadians((double)(mc.player.getYaw() + 90.0F)));
      double pitch = Math.sin(Math.toRadians((double)(mc.player.getYaw() + 90.0F)));
      double x = mc.player.getX();
      double y = mc.player.getY();
      double z = mc.player.getZ();
      mc.player.setPosition(x + (double)distance * yaw, y, z + (double)distance * pitch);
      return distance == 1.0F ? "HClipped keyCodec block" : "HClipped %s blocks".formatted(new Object[]{args[1]});
   }
}
