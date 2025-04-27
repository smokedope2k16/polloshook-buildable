package me.pollos.polloshook.api.command.args;

import java.io.File;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.util.logging.ClientLogger;

public class FileArgument extends Argument {
   private final File file;

   public FileArgument(String label) {
      super(label);
      this.file = null;
   }

   public FileArgument(String label, File file) {
      super(label);
      this.file = file;
   }

   public String predict(String currentArg) {
      File[] files = PollosHook.DIRECTORY.listFiles();
      if (files == null) {
         ClientLogger.getLogger().warn("No files found");
         return super.predict(currentArg);
      } else {
         if (this.file == null) {
            String[] spl = currentArg.split("/");
            boolean notSplit = !currentArg.contains("/");
            File child = new File(PollosHook.DIRECTORY, spl[0]);
            int var8;
            if (notSplit) {
               File[] var6 = files;
               int var7 = files.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  File f = var6[var8];
                  if (f.exists() && f.getName().toLowerCase().startsWith(currentArg.toLowerCase())) {
                     return f.getName();
                  }
               }
            } else if (child.exists() && spl.length > 1) {
               String prefix = spl[1].toLowerCase();
               File[] var16 = child.listFiles();
               var8 = var16.length;

               for(int var17 = 0; var17 < var8; ++var17) {
                  File f = var16[var17];
                  if (f.exists() && f.getName().toLowerCase().startsWith(prefix)) {
                     return spl[0] + "/" + f.getName();
                  }
               }
            }
         } else {
            File[] var11 = this.file.listFiles();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               File f = var11[var13];
               if (f.exists() && f.getName().toLowerCase().startsWith(currentArg.toLowerCase())) {
                  return f.getName();
               }
            }
         }

         return super.predict(currentArg);
      }
   }
}
