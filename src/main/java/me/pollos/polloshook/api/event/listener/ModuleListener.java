package me.pollos.polloshook.api.event.listener;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.impl.module.player.avoid.Avoid;
import me.pollos.polloshook.impl.module.player.avoid.CollisionShapeShapeEvent;

public abstract class ModuleListener<M, E> extends Listener<E> implements Minecraftable {
   protected final M module;

   public ModuleListener(M module, Class<? super E> target) {
      this(module, target, 10);
   }

   public ModuleListener(M module, Class<? super E> target, int priority) {
      this(module, target, priority, (Class)null);
   }

   public ModuleListener(M module, Class<? super E> target, Class<?> type) {
      this(module, target, 10, type);
   }

   public ModuleListener(M module, Class<? super E> target, int priority, Class<?> type) {
      super(target, priority, type);
      this.module = module;
   }

   public ModuleListener(Avoid module, Class<CollisionShapeShapeEvent> eventClass) {
      this((M) module, (Class<? super E>) eventClass);
  }
  
}
