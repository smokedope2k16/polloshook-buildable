package me.pollos.polloshook.api.command.args;

import java.util.Iterator;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.macro.SimpleMacro;
import me.pollos.polloshook.api.managers.Managers;

public class MacroArgument extends Argument {
   public MacroArgument(String label) {
      super(label);
   }

   public String predict(String currentArg) {
      Iterator var2 = Managers.getMacroManager().getSimpleMacros().iterator();

      SimpleMacro simpleMacro;
      do {
         if (!var2.hasNext()) {
            return currentArg;
         }

         simpleMacro = (SimpleMacro)var2.next();
      } while(!simpleMacro.getLabel().toLowerCase().startsWith(currentArg.toLowerCase()));

      return simpleMacro.getLabel();
   }
}
