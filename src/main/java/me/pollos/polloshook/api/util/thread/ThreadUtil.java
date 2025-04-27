package me.pollos.polloshook.api.util.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import me.pollos.polloshook.api.interfaces.Minecraftable;

public class ThreadUtil implements Minecraftable {
   public static final ThreadFactory FACTORY = newDaemonThreadFactoryBuilder().setNameFormat("polloshook-Thread-%d").build();

   public static ScheduledExecutorService newDaemonScheduledExecutor(String name) {
      ThreadFactoryBuilder factory = newDaemonThreadFactoryBuilder();
      factory.setNameFormat("polloshook-" + name + "-%d");
      return Executors.newSingleThreadScheduledExecutor(factory.build());
   }

   public static ExecutorService newDaemonCachedThreadPool() {
      return Executors.newCachedThreadPool(FACTORY);
   }

   public static ExecutorService newFixedThreadPool(int size) {
      ThreadFactoryBuilder factory = newDaemonThreadFactoryBuilder();
      factory.setNameFormat("polloshook-Fixed-%d");
      return Executors.newFixedThreadPool(Math.max(size, 1), factory.build());
   }

   public static ThreadFactoryBuilder newDaemonThreadFactoryBuilder() {
      ThreadFactoryBuilder factory = new ThreadFactoryBuilder();
      factory.setDaemon(true);
      return factory;
   }
}
