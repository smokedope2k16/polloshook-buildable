package me.pollos.polloshook.api.util.thread.interfaces;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import me.pollos.polloshook.api.util.thread.ThreadUtil;

public interface GlobalExecutor {
   ExecutorService EXECUTOR = ThreadUtil.newDaemonCachedThreadPool();
   ExecutorService FIXED_EXECUTOR = ThreadUtil.newFixedThreadPool((int)((double)Runtime.getRuntime().availableProcessors() / 1.5D));
   ScheduledExecutorService SCHEDULED_EXECUTOR = ThreadUtil.newDaemonScheduledExecutor("polloshook-Task");
}
