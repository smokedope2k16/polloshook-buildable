package me.pollos.polloshook.impl.command.modules.configs;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.args.FileArgument;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.config.modules.ModuleConfig;

public class ConfigCommand extends Command {
   public ConfigCommand() {
      super(new String[]{"Config", "configs", "con"}, new Argument("[save/load/delete/clear/list]") {
         public String predict(String currentArg) {
            if (currentArg.toLowerCase().startsWith("s")) {
               return "save";
            } else if (currentArg.toLowerCase().startsWith("l") && !currentArg.toLowerCase().startsWith("li")) {
               return "load";
            } else if (currentArg.toLowerCase().startsWith("li")) {
               return "list";
            } else if (currentArg.toLowerCase().startsWith("d")) {
               return "delete";
            } else {
               return currentArg.toLowerCase().startsWith("c") ? "clear" : currentArg;
            }
         }
      }, new FileArgument("[config]", PollosHook.CONFIGS));
   }

   public String execute(String[] args) {
      String args1 = args[1];
      String info;
      if (args1.equalsIgnoreCase("LIST")) {
         StringJoiner stringJoiner = new StringJoiner("\n");
         ((List)Objects.requireNonNull(ModuleConfig.getConfigList())).forEach((cfg) -> {
            stringJoiner.add(cfg.toString());
         });
         info = String.format(String.valueOf(stringJoiner));
         return String.format("Configs (%s):\n%s", ModuleConfig.getConfigList().size(), info.replace("..", "."));
      } else {
         String args2;
         if (args1.equalsIgnoreCase("CLEAR")) {
            args2 = this.getString();
            Managers.getConfigManager().getRegistry().removeIf((configurable) -> {
               return configurable instanceof ModuleConfig;
            });
            ModuleConfig.getConfigList().clear();
            return args2;
         } else if (args1.equalsIgnoreCase("DELETE")) {
            args2 = args[2];
            info = this.getInfo();
            File f = this.getFileByLabel(args2);
            if (f != null) {
               if (f.exists()) {
                  try {
                     boolean bl = f.delete();
                     if (bl) {
                        info = "Deleting file %s".formatted(new Object[]{f.getName()});
                        Managers.getConfigManager().getRegistry().removeIf((c) -> {
                           if (c instanceof ModuleConfig) {
                              ModuleConfig moduleConfig = (ModuleConfig)c;
                              return moduleConfig.getLabel().equalsIgnoreCase(f.getName());
                           } else {
                              return false;
                           }
                        });
                     } else {
                        info = "Failed to delete file";
                     }
                  } catch (Exception var7) {
                     info = this.getInfo();
                  }
               }

               return info;
            } else {
               return "No file with name %s".formatted(new Object[]{args1});
            }
         } else {
            return this.getString(args, args1);
         }
      }
   }

   private String getString(String[] args, String args1) {
      String result = this.getInfo();
      String name = args[2];
      String label = name.endsWith(".cfg") ? name : name + ".cfg";
      ModuleConfig config = new ModuleConfig(label);
      String var7 = args1.toUpperCase();
      byte var8 = -1;
      switch(var7.hashCode()) {
      case 2342118:
         if (var7.equals("LOAD")) {
            var8 = 1;
         }
         break;
      case 2537853:
         if (var7.equals("SAVE")) {
            var8 = 0;
         }
      }

      switch(var8) {
      case 0:
         config.save();
         result = String.format("Config %s has been saved", name);
         break;
      case 1:
         config.load();
         result = String.format("Config %s has been loaded", name);
      }

      return result;
   }

   private String getString() {
      String info = this.getInfo();
      Iterator var2 = ((List)Objects.requireNonNull(ModuleConfig.getConfigList())).iterator();

      while(var2.hasNext()) {
         File f = (File)var2.next();
         if (f.exists()) {
            try {
               boolean bl = f.delete();
               if (bl) {
                  info = "Deleting %s files".formatted(new Object[]{ModuleConfig.getConfigList().size()});
               } else {
                  info = "Failed to delete file";
               }
            } catch (Exception var5) {
               info = this.getInfo();
            }
         }
      }

      return info;
   }

   private File getFileByLabel(String s) {
      File[] files = PollosHook.CONFIGS.listFiles();
      if (files == null) {
         return null;
      } else {
         File[] var3 = files;
         int var4 = files.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File f = var3[var5];
            if (f.getName().equalsIgnoreCase(s)) {
               return f;
            }
         }

         return null;
      }
   }
}
