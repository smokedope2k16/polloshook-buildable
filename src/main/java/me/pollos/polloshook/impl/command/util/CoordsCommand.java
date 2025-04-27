package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Command;

public class CoordsCommand extends Command {
   public CoordsCommand() {
      super(new String[]{"GetCoords", "getcoord"});
   }

   public String execute(String[] args) {
      mc.keyboard.setClipboard(mc.player.getBlockPos().toShortString());
      return "Copied coords to clipboard";
   }
}
