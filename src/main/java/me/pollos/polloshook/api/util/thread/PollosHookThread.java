package me.pollos.polloshook.api.util.thread;


import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.thread.interfaces.GlobalExecutor;
import me.pollos.polloshook.api.util.thread.interfaces.SafeRunnable;

public final class PollosHookThread implements GlobalExecutor {
   public static void submit(SafeRunnable runnable) {
      submitRunnable(runnable);
   }

   public static void submitRunnable(Runnable runnable) {
      EXECUTOR.submit(runnable);
   }

   public static void shutDown() {
      EXECUTOR.shutdown();
   }

   public static Thread newShutdownHookThread() {
      return new Thread("Shutdown Hook Thread") {
         public void run() {
            Managers.getModuleManager().getAllModules().forEach(Module::onShutdown);
            Managers.getConfigManager().save();
         }
      };
   }

   
   private PollosHookThread() {
      throw new UnsupportedOperationException("This is keyCodec utility class and cannot be instantiated");
   }
}
