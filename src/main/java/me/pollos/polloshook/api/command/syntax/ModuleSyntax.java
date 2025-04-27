package me.pollos.polloshook.api.command.syntax;

import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.util.CommandUtil;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.value.value.Value;

public class ModuleSyntax extends Argument {
   private final Module module;

   public ModuleSyntax(Module module) {
      super("[value/preset/list/export/import%s]".formatted(
          module instanceof CommandModule ? 
          "/" + ((CommandModule)module).getCommand().getLabel() : 
          ""));
      this.module = module;
  }

   public String predict(String currentArg) {
      Value<?> value = (Value)CommandUtil.getLabeledStartingWith(currentArg, this.module.getValues());
      Module var4 = this.module;
      if (var4 instanceof CommandModule) {
         CommandModule commandModule = (CommandModule)var4;
         String[] var8 = commandModule.getCommand().getAliases();
         int var5 = var8.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String alias = var8[var6];
            if (TextUtil.startsWith(alias, currentArg)) {
               return alias;
            }
         }
      }

      if (value != null) {
         return this.getCorrectAlias(value);
      } else if (currentArg.toLowerCase().startsWith("p")) {
         return "preset";
      } else if (currentArg.toLowerCase().startsWith("l")) {
         return "list";
      } else if (currentArg.toLowerCase().startsWith("e")) {
         return "export";
      } else {
         return currentArg.toLowerCase().startsWith("i") ? "import" : currentArg;
      }
   }

   private String getCorrectAlias(Value<?> value) {
      try {
         if (value.getLabel().contains(" ")) {
            String thing = value.getAliases()[1];
            if (TextUtil.isNullOrEmpty(thing)) {
               return value.getLabel().replace(" ", "");
            }

            return value.getAliases()[1];
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      return value.getLabel();
   }
}
