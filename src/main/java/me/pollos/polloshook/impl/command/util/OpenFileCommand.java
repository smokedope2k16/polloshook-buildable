package me.pollos.polloshook.impl.command.util;

import java.io.File;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.args.FileArgument;
import me.pollos.polloshook.api.command.core.Command;
import net.minecraft.util.Util;

public class OpenFileCommand extends Command {
   public OpenFileCommand() {
      super(new String[]{"OpenFile", "file"}, new FileArgument("[file]"));
   }

   public String execute(String[] args) {
      if (args.length < 2) {
         return this.getInfo();
      } else {
         String relative = args[1];
         File file = new File(PollosHook.DIRECTORY, relative);
         if (file.exists()) {
            Util.getOperatingSystem().open(file.toURI());
            return "Opened %s (%s)".formatted(new Object[]{file.isFile() ? "file" : "dir", file.getName()});
         } else {
            return "No file labeled (%s)".formatted(new Object[]{relative});
         }
      }
   }
}
