package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<Freecam, TickEvent> {
   public ListenerTick(Freecam module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (mc.world == null || mc.player == null) {
         ((Freecam)this.module).setEnabled(false);
      }

   }
}
