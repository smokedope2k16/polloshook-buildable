package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.movement.SwimEvent;

public class ListenerSwim extends ModuleListener<Manager, SwimEvent> {
   public ListenerSwim(Manager module) {
      super(module, SwimEvent.class);
   }

   public void call(SwimEvent event) {
      if ((Boolean)((Manager)this.module).noSwim.getValue()) {
         event.setCanceled(true);
      }

   }
}
