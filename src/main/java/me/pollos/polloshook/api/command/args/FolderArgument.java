package me.pollos.polloshook.api.command.args;

import java.io.File;
import me.pollos.polloshook.api.command.core.Argument;

public class FolderArgument extends Argument {
   private final File file;

   public FolderArgument(String label, File file) {
      super(label);
      this.file = file;
   }

   public String predict(String currentArg) {
      File[] files = this.file.listFiles();
      if (files == null) {
         return super.predict(currentArg);
      } else {
         File[] var3 = files;
         int var4 = files.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File f = var3[var5];
            if (f.isDirectory() && f.getName().toLowerCase().startsWith(currentArg.toLowerCase())) {
               return f.getName();
            }
         }

         return super.predict(currentArg);
      }
   }
}
