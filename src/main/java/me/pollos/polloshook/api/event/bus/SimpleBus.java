package me.pollos.polloshook.api.event.bus;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import me.pollos.polloshook.api.event.bus.api.EventBus;
import me.pollos.polloshook.api.event.bus.api.IListener;
import me.pollos.polloshook.api.event.bus.api.Subscriber;
import me.pollos.polloshook.api.interfaces.Minecraftable;

public final class SimpleBus implements EventBus, Minecraftable {
   private final Map<Class<?>, List<IListener>> listeners = new ConcurrentHashMap();
   private final Set<Subscriber> subscribers = Collections.newSetFromMap(new ConcurrentHashMap());
   private final Set<IListener> subbedlisteners = Collections.newSetFromMap(new ConcurrentHashMap());

   public void dispatch(Object object) {
      List<IListener> listening = (List)this.listeners.get(object.getClass());
      if (listening != null) {
         Iterator var3 = listening.iterator();

         while(var3.hasNext()) {
            IListener listener = (IListener)var3.next();
            listener.call(object);
         }
      }

   }

   public void dispatch(Object object, Class<?> type) {
      List<IListener> listening = (List)this.listeners.get(object.getClass());
      if (listening != null) {
         Iterator var4 = listening.iterator();

         while(true) {
            IListener listener;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               listener = (IListener)var4.next();
            } while(listener.getType() != null && listener.getType() != type);

            listener.call(object);
         }
      }
   }

   public void register(IListener<?> listener) {
      if (this.subbedlisteners.add(listener)) {
         this.addAtPriority(listener, (List)this.listeners.computeIfAbsent(listener.getTarget(), (v) -> {
            return new CopyOnWriteArrayList();
         }));
      }

   }

   public void unregister(IListener listener) {
      if (this.subbedlisteners.remove(listener)) {
         List<IListener> list = (List)this.listeners.get(listener.getTarget());
         if (list != null) {
            list.remove(listener);
         }
      }

   }

   public void dispatchReversed(Object object, Class<?> type) {
      List<IListener> list = (List)this.listeners.get(object.getClass());
      if (list != null) {
         ListIterator li = list.listIterator(list.size());

         while(true) {
            IListener l;
            do {
               do {
                  if (!li.hasPrevious()) {
                     return;
                  }

                  l = (IListener)li.previous();
               } while(l == null);
            } while(l.getType() != null && l.getType() != type);

            l.call(object);
         }
      }
   }

   public void subscribe(Object object) {
      if (object instanceof Subscriber) {
         Subscriber subscriber = (Subscriber)object;
         Iterator var3 = subscriber.getListeners().iterator();

         while(var3.hasNext()) {
            IListener<?> listener = (IListener)var3.next();
            this.register(listener);
         }

         this.subscribers.add(subscriber);
      }

   }

   public void unsubscribe(Object object) {
      if (object instanceof Subscriber) {
         Subscriber subscriber = (Subscriber)object;
         Iterator var3 = subscriber.getListeners().iterator();

         while(var3.hasNext()) {
            IListener<?> listener = (IListener)var3.next();
            this.unregister(listener);
         }

         this.subscribers.remove(subscriber);
      }

   }

   public boolean isSubscribed(Object object) {
      if (object instanceof Subscriber) {
         return this.subscribers.contains(object);
      } else {
         return object instanceof IListener ? this.subbedlisteners.contains(object) : false;
      }
   }

   public boolean hasSubscribers(Class<?> clazz) {
      List<IListener> listening = (List)this.listeners.get(clazz);
      return listening != null && !listening.isEmpty();
   }

   public boolean hasSubscribers(Class<?> clazz, Class<?> type) {
      List<IListener> listening = (List)this.listeners.get(clazz);
      return listening != null && listening.stream().anyMatch((listener) -> {
         return listener.getType() == null || listener.getType() == type;
      });
   }

   private void addAtPriority(IListener<?> listener, List<IListener> list) {
      int index;
      for(index = 0; index < list.size() && listener.getPriority() < ((IListener)list.get(index)).getPriority(); ++index) {
      }

      list.add(index, listener);
   }
}
