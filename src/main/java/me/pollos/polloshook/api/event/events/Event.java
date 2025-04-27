package me.pollos.polloshook.api.event.events;

import me.pollos.polloshook.PollosHook;

public class Event {
   private boolean canceled = false;

   public boolean isCanceled() {
      return this.canceled;
   }

   public void setCanceled(boolean canceled) {
      this.canceled = canceled;
   }

   public void dispatch() {
      PollosHook.getEventBus().dispatch(this);
   }

   public void dispatch(Class<?> type) {
      PollosHook.getEventBus().dispatch(this, type);
   }
}