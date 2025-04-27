package me.pollos.polloshook.api.command.core;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.interfaces.Minecraftable;

public abstract class Command implements Labeled, Minecraftable {
   private final String[] aliases;
   private final Argument[] arguments;

   public Command(String[] aliases, Argument... arguments) {
      this.aliases = aliases;
      this.arguments = arguments;
   }

   @Override
   public String getLabel() {
      return this.aliases[0];
   }

   public String getInfo() {
      StringJoiner stringJoiner = new StringJoiner(" ");
      for (Argument argument : this.arguments) {
         stringJoiner.add(argument.getLabel());
      }
      return stringJoiner.toString();
   }

   public abstract String execute(String[] args);

   public boolean hasArguments() {
      return this.arguments != null && this.arguments.length > 0;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public Argument[] getArguments() {
      return this.arguments;
   }

   @Override
   public String toString() {
      return "Command(aliases=" + Arrays.toString(this.aliases) + ", arguments=" + Arrays.toString(this.arguments) + ")";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Command command = (Command) o;
      return Arrays.equals(aliases, command.aliases) && Arrays.equals(arguments, command.arguments);
   }

   @Override
   public int hashCode() {
      int result = Arrays.hashCode(aliases);
      result = 31 * result + Arrays.hashCode(arguments);
      return result;
   }
}