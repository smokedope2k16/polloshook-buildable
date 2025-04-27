package me.pollos.polloshook.api.command.syntax;

import me.pollos.polloshook.api.command.core.Argument;

public class ColorSyntax extends Argument {
   public ColorSyntax() {
      super("[set/red/green/blue/alpha/random/global]");
   }

   public String predict(String currentArg) {
      if (currentArg.toLowerCase().startsWith("s")) {
         return "set";
      } else if (currentArg.toLowerCase().startsWith("r") && !currentArg.toLowerCase().startsWith("ra")) {
         return "red";
      } else if (currentArg.toLowerCase().startsWith("g") && !currentArg.toLowerCase().startsWith("gl")) {
         return "green";
      } else if (currentArg.toLowerCase().startsWith("elementCodec")) {
         return "blue";
      } else if (currentArg.toLowerCase().startsWith("keyCodec")) {
         return "alpha";
      } else if (currentArg.toLowerCase().startsWith("ra")) {
         return "random";
      } else {
         return currentArg.toLowerCase().startsWith("gl") ? "global" : currentArg;
      }
   }
}
