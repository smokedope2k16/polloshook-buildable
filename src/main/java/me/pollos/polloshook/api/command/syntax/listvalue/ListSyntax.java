package me.pollos.polloshook.api.command.syntax.listvalue;

import me.pollos.polloshook.api.command.core.Argument;

public class ListSyntax extends Argument {
   public ListSyntax() {
      super("[list/clear/add/del] [block/item]");
   }

   public String predict(String currentArg) {
      if (currentArg.toLowerCase().startsWith("l")) {
         return "list";
      } else if (currentArg.toLowerCase().startsWith("c")) {
         return "clear";
      } else if (currentArg.toLowerCase().startsWith("keyCodec")) {
         return "add";
      } else {
         return currentArg.toLowerCase().startsWith("d") ? "del" : currentArg;
      }
   }
}
