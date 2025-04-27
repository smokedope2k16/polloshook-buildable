package me.pollos.polloshook.api.event.listener;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.thread.SafeUtil;

public abstract class SafeModuleListener<M, E> extends Listener<E> implements Minecraftable {
   protected final M module;

   public SafeModuleListener(M module, Class<? super E> target) {
      this(module, target, 10);
   }

   public SafeModuleListener(M module, Class<? super E> target, int priority) {
      this(module, target, priority, (Class)null);
   }

   public SafeModuleListener(M module, Class<? super E> target, Class<?> type) {
      this(module, target, 10, type);
   }

   public SafeModuleListener(M module, Class<? super E> target, int priority, Class<?> type) {
      super(target, priority, type);
      this.module = module;
   }

   public abstract void safeCall(E var1);

   public void call(E event) {
      SafeUtil.safe(mc, (player, level, interactionManager) -> {
         this.safeCall(event);
      });
   }
}
