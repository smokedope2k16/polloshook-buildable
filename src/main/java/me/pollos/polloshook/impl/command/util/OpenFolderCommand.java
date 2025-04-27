package me.pollos.polloshook.impl.command.util;

import java.io.File;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.args.FolderArgument;
import me.pollos.polloshook.api.command.core.Command;
import net.minecraft.util.Util;

public class OpenFolderCommand extends Command {
   public OpenFolderCommand() {
      super(new String[]{"OpenFolder", "folder"}, new FolderArgument("[folder]", PollosHook.DIRECTORY));
   }

   public String execute(String[] args) {
      if (args.length <= 1) {
         Util.getOperatingSystem().open(PollosHook.DIRECTORY.toURI());
         return "Opened %s".formatted(new Object[]{PollosHook.DIRECTORY.getName()});
      } else {
         String folderName = args[1].toLowerCase();
         File folder = new File(PollosHook.DIRECTORY, folderName);
         if (folder.exists() && folder.isDirectory()) {
            Util.getOperatingSystem().open(folder.toURI());
            return "Opened folder (%s)".formatted(new Object[]{folder.getName()});
         } else {
            return "No folder labeled (%s)".formatted(new Object[]{folderName});
         }
      }
   }
}
