package me.pollos.polloshook;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import me.pollos.polloshook.api.event.bus.SimpleBus;
import me.pollos.polloshook.api.event.bus.api.EventBus;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.SessionUtil;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.system.SystemStatus;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.api.util.thread.FileUtil;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.api.util.thread.execption.UnsupportedLoaderVersionException;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import me.pollos.polloshook.impl.config.modules.ModuleConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public final class PollosHook {
   public static final String NAME = "polloshook";
   public static final String NAME_UNICODE = "\ud83d\udc14";
   public static final String VERSION = "v2.8.5";
   public static final File DIRECTORY;
   public static final File MODULES;
   public static final File SPAMMERS;
   public static final File ELEMENTS;
   public static final File PACKETS;
   public static final File CONFIGS;
   private static final EventBus EVENT_BUS;
   private static SystemStatus SYSTEM_STATUS;
   private static ModuleConfig CURRENT_CONFIG;

   private PollosHook() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }

   static {
      DIRECTORY = new File(MinecraftClient.getInstance().runDirectory, "polloshook");
      MODULES = new File(DIRECTORY, "modules");
      SPAMMERS = new File(DIRECTORY, "spammers");
      ELEMENTS = new File(DIRECTORY, "elements");
      PACKETS = new File(DIRECTORY, "packets");
      CONFIGS = new File(DIRECTORY, "configs");
      EVENT_BUS = new SimpleBus();
   }

   public static void init() {
      final String currentVersion = FabricLoader.getInstance().getModContainer("fabricloader")
         .map(container -> container.getMetadata().getVersion().getFriendlyString())
         .orElse("unknown");
      if (!currentVersion.equals("0.16.9")) {
         final UnsupportedLoaderVersionException exec = new UnsupportedLoaderVersionException();
         ClientLogger.getLogger().report(exec.getMessage(), exec);
      }

      final long startTime = System.nanoTime() / 1000000L;
      ClientLogger.getLogger().info("Initializing...");
      if (!DIRECTORY.exists()) {
         final boolean mkdir = DIRECTORY.mkdir();
         ClientLogger.getLogger().info(String.format("%s client directory", mkdir ? "Created" : "Failed to create"));
      }

      if (!MODULES.exists()) {
         ClientLogger.getLogger().info(String.format("%s modules directory", MODULES.mkdir() ? "Created" : "Failed to create"));
      }

      if (!SPAMMERS.exists()) {
         ClientLogger.getLogger().info(String.format("%s spammers directory", SPAMMERS.mkdir() ? "Created" : "Failed to create"));
      }

      if (!ELEMENTS.exists()) {
         ClientLogger.getLogger().info(String.format("%s elements directory", ELEMENTS.mkdir() ? "Created" : "Failed to create"));
      }

      if (!PACKETS.exists()) {
         ClientLogger.getLogger().info(String.format("%s packets directory", PACKETS.mkdir() ? "Created" : "Failed to create"));
      }

      if (!CONFIGS.exists()) {
         ClientLogger.getLogger().info(String.format("%s configurations directory", CONFIGS.mkdir() ? "Created" : "Failed to create"));
      }

      if (MinecraftClient.getInstance().getSession().getUsername().startsWith("Player")) {
         SessionUtil.setSession(TextUtil.randomString(new Random().nextInt(3, 12)));
      }

      config().load();
      ClientLogger.getLogger().info("Loading managers...");
      Managers.init();
      Managers.getConfigManager().load();
      ClientLogger.getLogger().info("Managers loaded successfully");
      ClientLogger.getLogger().info(String.format("Initialized in %s milliseconds.", System.nanoTime() / 1000000L - startTime));
      Runtime.getRuntime().addShutdownHook(PollosHookThread.newShutdownHookThread());
   }

   public static void shutdown() {
      final long shutDownTime = System.nanoTime() / 1000000L;
      ClientLogger.getLogger().info("Shutting down...");
      Managers.getModuleManager().getAllModules().forEach(Module::onShutdown);
      Managers.getConfigManager().save();
      ClientLogger.getLogger().info("Client closed in %s milliseconds.".formatted(System.nanoTime() / 1000000L - shutDownTime));
      PollosHookThread.shutDown();
   }

   public static boolean isRunClient() {
      return GraphicsEnvironment.isHeadless() && !System.getProperty("user.name").equalsIgnoreCase("pollosxd");
   }

   public static boolean isFuture() {
      return FabricLoader.getInstance().isModLoaded("future");
   }

   public static EventBus getEventBus() {
      return EVENT_BUS;
   }

   public static SystemStatus getSystemStatus() {
      return SYSTEM_STATUS;
   }

   public static void setSystemStatus(final SystemStatus systemStatus) {
      SYSTEM_STATUS = systemStatus;
   }

   public static AbstractConfig config() {
      return new AbstractConfig("current_moduleconfig.txt") {
         @Override
         public void load() {
            FileUtil.handleFileCreation(this.getFile());

            try (BufferedReader reader = FileUtil.createBufferedReader(this.getFile())) {
               String line;
               while ((line = reader.readLine()) != null) {
                  final String currentLine = line;
                  File[] configFiles = Objects.requireNonNull(PollosHook.CONFIGS.listFiles());
                  for (File file : configFiles) {
                     if (file.getName().equalsIgnoreCase(currentLine)) {
                        Managers.getConfigManager().getRegistry().forEach(configurable -> {
                           if (configurable instanceof ModuleConfig) {
                              ModuleConfig moduleConfig = (ModuleConfig) configurable;
                              if (moduleConfig.getLabel().equalsIgnoreCase(currentLine)) {
                                 ClientLogger.getLogger().info("Setting current config");
                                 PollosHook.CURRENT_CONFIG = moduleConfig;
                              }
                           }
                        });
                     }
                  }
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

         @Override
         public void save() {
            FileUtil.handleFileCreation(this.getFile());

            try (BufferedWriter writer = FileUtil.createWriter(this.getFile())) {
               writer.write(PollosHook.CURRENT_CONFIG == null ? "None" : PollosHook.CURRENT_CONFIG.getLabel());
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      };
   }

   public static ModuleConfig getCurrentConfig() {
      return CURRENT_CONFIG;
   }

   public static void setCurrentConfig(final ModuleConfig currentConfig) {
      CURRENT_CONFIG = currentConfig;
   }
}