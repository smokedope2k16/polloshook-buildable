package me.pollos.polloshook.impl.command.macro;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;
import me.pollos.polloshook.api.command.args.KeyArgument;
import me.pollos.polloshook.api.command.args.MacroArgument;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.command.util.CommandUtil;
import me.pollos.polloshook.api.macro.SimpleMacro;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.obj.MessageSender;
import net.minecraft.util.Formatting;

public class MacroCommand extends Command {
   public MacroCommand() {
      super(new String[]{"Macro", "m", "macros"}, new Argument("[add/del/list/clear]") {
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
      }, new MacroArgument("[label]"), new KeyArgument("[bind]"), new Argument("[execute]"));
   }

   public String execute(String[] args) {
      String bind;
      if (args.length == 3) {
         bind = args[2].toUpperCase();
         if (args[1].equalsIgnoreCase("del")) {
            if (Managers.getMacroManager().containsSimple(bind)) {
               Managers.getMacroManager().removeSimple(bind);
               return String.format("Removed simple macro labeled [%s]", bind);
            }

            return String.format("There is no simple macro labeled [%s]", bind);
         }
      }

      String message;
      if (args.length >= 4) {
         bind = args[3].toUpperCase();
         message = CommandUtil.concatenate(args, 4);
         if (args[1].equalsIgnoreCase("add")) {
            Managers.getMacroManager().getSimpleMacros().add(new SimpleMacro(args[2], KeyboardUtil.getKeyNumberFromName("key.keyboard." + bind.toLowerCase()), new MessageSender(message)));
            return String.format("Added simple macro binded to key [%s] labeled to [%s] that executes [%s]", bind, args[2], message);
         }
      }

      if (args[1].equalsIgnoreCase("list")) {
         StringJoiner stringJoiner = new StringJoiner(", ");
         Stream<String> simpleMacroStream = Managers.getMacroManager().getSimpleMacros().stream().map((macro) -> {
             return "\n%s%s %s[%s%s%s] [%s%s%s]".formatted(new Object[]{Formatting.GRAY, macro.getLabel(), Formatting.GRAY, Formatting.GRAY, macro.getChatMacro().getMessage(), Formatting.GRAY, Formatting.GRAY, KeyboardUtil.getKeyNameFromNumber(macro.getKey()), Formatting.GRAY});
         });
         Objects.requireNonNull(stringJoiner);
         simpleMacroStream.forEach(stringJoiner::add);
         message = String.format(String.valueOf(stringJoiner));
         return String.format("Simple Macros (%s): %s", Managers.getMacroManager().getSimpleMacros().size(), message);
     }else if (args[1].equalsIgnoreCase("clear")) {
         Managers.getMacroManager().getSimpleMacros().clear();
         return "Cleared simple all macros";
      } else {
         return this.getInfo();
      }
   }
}