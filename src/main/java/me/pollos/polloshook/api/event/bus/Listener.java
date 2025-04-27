package me.pollos.polloshook.api.event.bus;

import me.pollos.polloshook.api.event.bus.api.IListener;
import me.pollos.polloshook.api.interfaces.Minecraftable;

public abstract class Listener<E> implements IListener<E>, Minecraftable {
   private final Class<? super E> event;
   private final Class<?> type;
   private final int priority;

   public Listener(Class<? super E> event) {
      this(event, 10, (Class)null);
   }

   public Listener(Class<? super E> event, Class<?> type) {
      this(event, 10, type);
   }

   public Listener(Class<? super E> event, int priority) {
      this(event, priority, (Class)null);
   }

   public Listener(Class<? super E> target, int priority, Class<?> type) {
      this.priority = priority;
      this.event = target;
      this.type = type;
   }

   public int getPriority() {
      return this.priority;
   }

   public Class<? super E> getTarget() {
      return this.event;
   }

   public Class<?> getType() {
      return this.type;
   }
}
