package me.pollos.polloshook.impl.config.base;

import java.io.File;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.interfaces.Configurable;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.managers.Managers;

public abstract class AbstractConfig implements Labeled, Configurable {
   private final String label;
   private final File file;
   private final File directory;

   public AbstractConfig(String label) {
      this.label = label;
      this.directory = PollosHook.DIRECTORY;
      this.file = new File(this.directory, label);
      Managers.getConfigManager().getRegistry().add(this);
   }

   public AbstractConfig(String label, File directory) {
      this.label = label;
      this.file = new File(directory, label);
      this.directory = directory;
      Managers.getConfigManager().getRegistry().add(this);
   }

   public abstract void load();

   public abstract void save();

   
   public String getLabel() {
      return this.label;
   }

   
   public File getFile() {
      return this.file;
   }

   
   public File getDirectory() {
      return this.directory;
   }
}
