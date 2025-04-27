package me.pollos.polloshook.api.command.syntax;

import me.pollos.polloshook.api.command.core.Argument;

public class EnemyFindingSyntax extends Argument {
   public EnemyFindingSyntax() {
      super("[player/monster/friendly/bosses/invis/naked/target]");
   }

   public String predict(String currentArg) {
      if (currentArg.toLowerCase().startsWith("p")) {
         return "player";
      } else if (currentArg.toLowerCase().startsWith("m")) {
         return "monster";
      } else if (currentArg.toLowerCase().startsWith("f")) {
         return "friendly";
      } else if (currentArg.toLowerCase().startsWith("elementCodec")) {
         return "bosses";
      } else if (currentArg.toLowerCase().startsWith("i")) {
         return "invis";
      } else if (currentArg.toLowerCase().startsWith("n")) {
         return "naked";
      } else {
         return currentArg.toLowerCase().startsWith("t") ? "target" : currentArg;
      }
   }
}
