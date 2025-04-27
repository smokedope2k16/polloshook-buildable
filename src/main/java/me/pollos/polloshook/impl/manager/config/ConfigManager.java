package me.pollos.polloshook.impl.manager.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.interfaces.Configurable;
import me.pollos.polloshook.api.interfaces.Initializable;
import me.pollos.polloshook.impl.config.modules.ModuleConfig;

public class ConfigManager implements Configurable, Initializable {
   private final ArrayList<Configurable> registry = new ArrayList();

   public void init() {
      this.load();
      List<File> files = ModuleConfig.getConfigList();
      if (files != null && !files.isEmpty()) {
         Iterator var2 = files.iterator();

         while(var2.hasNext()) {
            File configFile = (File)var2.next();
            if (configFile.exists() && configFile.isFile()) {
               ModuleConfig moduleConfig = new ModuleConfig(configFile.getName());
               moduleConfig.setFile(configFile);
            }
         }

      }
   }

   public ConfigManager start(String start) {
      this.info(start);
      return this;
   }

   public ConfigManager finish(String finish) {
      this.info(finish);
      return this;
   }

   public void save() {
      this.registry.forEach((config) -> {
         if (!(config instanceof ModuleConfig)) {
            config.save();
         }

      });
   }

   public void load() {
      this.registry.forEach((config) -> {
         if (!(config instanceof ModuleConfig)) {
            config.load();
         }

      });
   }

   public Collection<ModuleConfig> getModuleConfigs() {
      Collection<ModuleConfig> collection = new ArrayList();
      Iterator var2 = this.registry.iterator();

      while(var2.hasNext()) {
         Configurable config = (Configurable)var2.next();
         if (config instanceof ModuleConfig) {
            collection.add((ModuleConfig)config);
         }
      }

      return collection;
   }

   
   public ArrayList<Configurable> getRegistry() {
      return this.registry;
   }
}
