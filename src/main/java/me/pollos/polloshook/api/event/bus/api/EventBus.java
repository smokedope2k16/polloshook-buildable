package me.pollos.polloshook.api.event.bus.api;

public interface EventBus {
   int DEFAULT_PRIORITY = 10;

   void dispatch(Object var1);

   void dispatch(Object var1, Class<?> var2);

   void register(IListener<?> var1);

   void unregister(IListener<?> var1);

   void dispatchReversed(Object var1, Class<?> var2);

   void subscribe(Object var1);

   void unsubscribe(Object var1);

   boolean isSubscribed(Object var1);

   boolean hasSubscribers(Class<?> var1);

   boolean hasSubscribers(Class<?> var1, Class<?> var2);
}
