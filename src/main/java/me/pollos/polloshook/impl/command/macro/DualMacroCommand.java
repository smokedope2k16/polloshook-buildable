package me.pollos.polloshook.impl.command.macro;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;
import me.pollos.polloshook.api.command.args.KeyArgument;
import me.pollos.polloshook.api.command.args.MacroArgument;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.macro.DualMacro;
import me.pollos.polloshook.api.macro.SimpleMacro;
import me.pollos.polloshook.api.macro.records.DualRecord;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import net.minecraft.util.Formatting;

public class DualMacroCommand extends Command {
   public DualMacroCommand() {
      super(new String[]{"DualMacro", "dualmacros"}, new Argument("[add/del/list/clear]") {
         public String predict(String currentArg) {
            if (currentArg.toLowerCase().startsWith("keyCodec")) {
               return "add";
            } else if (currentArg.toLowerCase().startsWith("d")) {
               return "del";
            } else if (currentArg.toLowerCase().startsWith("l")) {
               return "list";
            } else {
               return currentArg.toLowerCase().startsWith("c") ? "clear" : currentArg;
            }
         }
      }, new Argument("[label]"), new KeyArgument("[bind]"), new MacroArgument("[macro1]"), new MacroArgument("[macro2]"));
   }

   public String execute(String[] args) {
      String label;
      if (args.length == 3) {
         label = args[2].toUpperCase();
         if (args[1].equalsIgnoreCase("del")) {
            if (Managers.getMacroManager().containsDual(label)) {
               Managers.getMacroManager().removeDual(label);
               return String.format("Removed dual macro labeled [%s]", label);
            }

            return String.format("There is no dual macro labeled [%s]", label);
         }
      }

      String bind;
      if (args.length >= 4) {
         label = args[2];
         bind = args[3].toUpperCase();
         String first = args[4];
         String second = args[5];
         SimpleMacro firstTarget = Managers.getMacroManager().getSimple(first);
         if (firstTarget == null) {
            return "No simple macro labeled %s".formatted(new Object[]{first});
         }

         SimpleMacro secondTarget = Managers.getMacroManager().getSimple(second);
         if (secondTarget == null) {
            return "No simple macro labeled %s".formatted(new Object[]{second});
         }

         DualRecord dual = new DualRecord(firstTarget, secondTarget);
         firstTarget.setPaused(true);
         secondTarget.setPaused(true);
         if (args[1].equalsIgnoreCase("add")) {
            Managers.getMacroManager().getDualMacros().add(new DualMacro(label, KeyboardUtil.getKeyNumberFromName("key.keyboard." + bind.toLowerCase()), dual));
            return String.format("Added Dual Macro binded to [%s] labeled [%s] utilizing 2 macros [%s, %s]".formatted(new Object[]{bind.toUpperCase(), label, dual.first().getLabel(), dual.second().getLabel()}));
         }
      }

      if (args[1].equalsIgnoreCase("list")) {
         StringJoiner stringJoiner = new StringJoiner(", ");
         Stream<String> macroStream = Managers.getMacroManager().getDualMacros().stream().map((macro) -> {
             String grayFormatting = String.valueOf(Formatting.GRAY);
             return "\n" + grayFormatting + macro.getLabel() + grayFormatting + " [" + grayFormatting + KeyboardUtil.getKeyNameFromNumber(macro.getKey()) + grayFormatting + "] [" + grayFormatting + macro.getFirst().getLabel() + ", " + macro.getSecond().getLabel() + grayFormatting + "]";
         });
         Objects.requireNonNull(stringJoiner);
         macroStream.forEach(stringJoiner::add);
         bind = String.format(String.valueOf(stringJoiner));
         return String.format("Macros (%s): %s", Managers.getMacroManager().getSimpleMacros().size(), bind);
     } else if (args[1].equalsIgnoreCase("clear")) {
         Managers.getMacroManager().getDualMacros().clear();
         return "Cleared all dual macros";
      } else {
         return this.getInfo();
      }
   }
}