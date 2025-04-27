package me.pollos.polloshook.impl.module.render.norender.util;


import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.value.value.Value;

public class NoRenderValue<E extends Event> extends Value<Boolean> {
   private final Class<E> eventClass;

   public NoRenderValue(Class<E> eventClass, Boolean value, String... aliases) {
      super(value, aliases);
      this.eventClass = eventClass;
      this.registerListener();
   }

   public void registerListener() {
      PollosHook.getEventBus().register(new Listener<E>(this.eventClass) {
         public void call(E event) {
            if ((Boolean)NoRenderValue.this.value) {
               event.setCanceled(true);
            }

         }
      });
   }

   
   public Class<E> getEventClass() {
      return this.eventClass;
   }
}
