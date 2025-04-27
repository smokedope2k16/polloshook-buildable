package me.pollos.polloshook.impl.command.modules;

import java.util.Iterator;
import java.util.StringJoiner;
import me.pollos.polloshook.api.command.args.ModuleArgument;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.preset.Preset;
import me.pollos.polloshook.api.util.text.TextUtil;

public class PresetCommand extends Command {
   public PresetCommand() {
      super(new String[]{"Preset", "set"}, new ModuleArgument("[mod]"), new Argument("[preset]") {
         public String predict(String currentArg) {
            return currentArg.equals("d") ? "default" : currentArg;
         }
      });
   }

   public String execute(String[] args) {
      Module module = Managers.getModuleManager().getModuleOrHUDByAlias(args[1]);
      if (module == null) {
         return "No such module exists";
      } else if (module.getPresets().size() < 1) {
         return "That module has no presets";
      } else {
         Preset presetInput = module.getPresetByLabel(args[2]);
         if (presetInput != null) {
            presetInput.execute();
            return String.format("Loaded %s preset for %s", presetInput.getLabel(), module.getLabel());
         } else {
            StringJoiner stringJoiner = new StringJoiner(", ");
            Iterator var5 = module.getPresets().iterator();

            while(var5.hasNext()) {
               Preset preset = (Preset)var5.next();
               stringJoiner.add(TextUtil.getFixedName(preset.getLabel()));
            }

            return String.format("Try: %s", stringJoiner);
         }
      }
   }
}
