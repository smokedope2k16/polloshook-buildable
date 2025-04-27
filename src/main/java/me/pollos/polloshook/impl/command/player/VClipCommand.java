package me.pollos.polloshook.impl.command.player;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;

public class VClipCommand extends Command {
   public VClipCommand() {
      super(new String[]{"VClip", "yclip", "verticalclip"}, new Argument("[amount]"));
   }

   public String execute(String[] args) {
      float parse = Float.parseFloat(args[1]);
      if (parse != 1.0F && parse != -1.0F) {
         mc.player.setPosition(mc.player.getX(), mc.player.getY() + (double)parse, mc.player.getZ());
         return "YClipped %s blocks".formatted(new Object[]{parse});
      } else {
         mc.player.setPosition(mc.player.getX(), mc.player.getY() + (double)parse, mc.player.getZ());
         return "YClipped keyCodec block %s".formatted(new Object[]{parse > 0.0F ? "upwards" : "downwards"});
      }
   }
}
