package me.pollos.polloshook.impl.command.util;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.command.util.CommandUtil;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.thread.PollosHookThread;

public class ExecuteInCommand extends Command {
   public ExecuteInCommand() {
      super(new String[]{"ExecuteIn", "executecmd", "executecommandin"}, new Argument("[ms]"), new ExecuteInCommand.CommandArgument());
   }

   public String execute(String[] args) {
      String args1 = args[1];
      String args2 = args[2];
      String[] cmdArgs = (String[])Arrays.copyOfRange(args, 2, args.length);
      Command cmd = this.getCommandByAlias(args2);
      if (cmd != null) {
         PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
            PollosHookThread.submit(() -> {
               ClientLogger.getLogger().log(cmd.execute(cmdArgs));
            });
         }, Long.parseLong(args1), TimeUnit.MILLISECONDS);
         return "Executing command...";
      } else {
         return "No command labeled %s".formatted(new Object[]{args2});
      }
   }

   private Command getCommandByAlias(String alias) {
      return (Command)Managers.getCommandManager().collectCommands().stream().filter((c) -> {
         String[] var2 = c.getAliases();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            if (s.equalsIgnoreCase(alias)) {
               return true;
            }
         }

         return false;
      }).findFirst().orElse((Command)null);
   }

   private static class CommandArgument extends Argument {
      public CommandArgument() {
         super("[command]");
      }

      public String predict(String currentArg) {
         Command cmd = (Command)CommandUtil.getLabeledStartingWith(currentArg, Managers.getCommandManager().getCommands());
         return cmd != null ? cmd.getLabel() : super.predict(currentArg);
      }
   }
}
