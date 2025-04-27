package me.pollos.polloshook.impl.command.util;

import java.util.StringJoiner;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;

public class HelpCommand extends Command {
   public HelpCommand() {
      super(new String[]{"help", "h"});
   }

   public String execute(String[] args) {
      StringJoiner stringJoiner = new StringJoiner(", ");
      Managers.getCommandManager().getCommands().forEach((module) -> {
         stringJoiner.add(module.getAliases()[0]);
      });
      return "List of commands you can use: %s".formatted(new Object[]{stringJoiner});
   }
}
