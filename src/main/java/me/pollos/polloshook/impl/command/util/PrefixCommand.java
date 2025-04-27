package me.pollos.polloshook.impl.command.util;

import me.pollos.polloshook.api.command.args.KeyArgument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;

public class PrefixCommand extends Command {
   public PrefixCommand() {
      super(new String[]{"Prefix", "p"}, new KeyArgument("[char]"));
   }

   public String execute(String[] args) {
      String prefix = args[1];
      if (prefix.equalsIgnoreCase(Managers.getCommandManager().getPrefix())) {
         return "That is already your prefix";
      } else {
         Managers.getCommandManager().setPrefix(prefix);
         return "%s is now your prefix".formatted(new Object[]{prefix});
      }
   }
}
