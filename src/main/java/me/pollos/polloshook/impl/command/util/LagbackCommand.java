package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.util.text.TextUtil;

public class LagbackCommand extends Command {
   private final String[] modes = new String[]{"packet", "motion", "move"};

   public LagbackCommand() {
      super(new String[]{"Lagback", "lag", "weee"}, new LagbackCommand.LagbackArgument(new String[]{"packet", "motion", "move"}));
   }

   public String execute(String[] args) {
      if (args.length != 0 && args.length <= 2) {
         String var2 = args[1].toUpperCase();
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -2014989386:
            if (var2.equals("MOTION")) {
               var3 = 1;
            }
            break;
         case -1942536056:
            if (var2.equals("PACKET")) {
               var3 = 0;
            }
            break;
         case 2372561:
            if (var2.equals("MOVE")) {
               var3 = 2;
            }
         }

         String var10000;
         switch(var3) {
         case 0:
            PacketUtil.move(mc.player.getX(), -1337.0D, mc.player.getZ(), true);
            var10000 = "Sending lagback packet...";
            break;
         case 1:
            MovementUtil.setYVelocity(1.0D, mc.player);
            var10000 = "Sending you upwards...";
            break;
         case 2:
            PacketUtil.move(mc.player.getX(), mc.player.getY() + 0.1D, mc.player.getZ(), true);
            mc.player.setVelocity(mc.player.getVelocity().subtract(0.0D, 0.05D, 0.0D));
            var10000 = "Sending movement lagback...";
            break;
         default:
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < this.modes.length; ++i) {
               String str = this.modes[i];
               String slash = i == this.modes.length - 1 ? "" : "/";
               builder.append(TextUtil.getFixedName(str)).append(slash);
            }

            var10000 = "Use (%s)".formatted(new Object[]{builder.toString()});
         }

         return var10000;
      } else {
         return "retard";
      }
   }

   public static class LagbackArgument extends Argument {
      private final String[] modes;

      public LagbackArgument(String[] modes) {
         super("[mode]");
         this.modes = modes;
      }

      public String predict(String currentArg) {
         if (currentArg != null && !currentArg.isEmpty()) {
            String[] var2 = this.modes;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String mode = var2[var4];
               if (mode.toLowerCase().startsWith(currentArg.toLowerCase())) {
                  return mode;
               }
            }

            return currentArg;
         } else {
            return currentArg;
         }
      }
   }
}
