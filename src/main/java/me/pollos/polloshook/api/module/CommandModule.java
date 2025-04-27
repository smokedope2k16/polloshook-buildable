package me.pollos.polloshook.api.module;

import java.util.Arrays;
import java.util.Objects;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;

public abstract class CommandModule extends ToggleableModule {
   private final Command command;

   public CommandModule(String[] aliases, Category category, String[] commandAliases, Argument... commandArgs) {
      super(aliases, category);
      this.command = new Command(commandAliases, commandArgs) {
         @Override
         public String execute(String[] args) {
            return CommandModule.this.onCommand(args);
         }
      };
      Managers.getCommandManager().registerModule(this.command, this);
   }

   public Command getCommand() {
      return this.command;
   }

   public boolean matching2Args(String[] args) {
      if (args.length < 2) {
         return false;
      }
      String arg0 = args[0];
      String arg1 = args[1];
      boolean matchLabel = false;
      for (String str : this.getAliases()) {
         if (arg0.replace(Managers.getCommandManager().getPrefix(), "").equalsIgnoreCase(str)) {
            matchLabel = true;
            break;
         }
      }
      boolean matchCommandLabel = false;
      for (String str : this.command.getAliases()) {
         if (arg1.equalsIgnoreCase(str)) {
            matchCommandLabel = true;
            break;
         }
      }
      return matchCommandLabel && matchLabel;
   }

   public abstract String onCommand(String[] args);

   @Override
   public String getInfo() {
      return this.command == null ? "No info" : this.command.getInfo();
   }

   @Override
   public String toString() {
      return "CommandModule(super=" + super.toString() + ", command=" + this.command + ")";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CommandModule that = (CommandModule) o;
      return Objects.equals(command, that.command);
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), command);
   }
}