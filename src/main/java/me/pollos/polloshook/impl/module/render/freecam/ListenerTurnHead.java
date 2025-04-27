package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerTurnHead extends ModuleListener<Freecam, Freecam.EntityTurnHeadEvent> {
   public ListenerTurnHead(Freecam module) {
      super(module, Freecam.EntityTurnHeadEvent.class);
   }

   public void call(Freecam.EntityTurnHeadEvent event) {
      event.setEntity(((Freecam)this.module).getRender());
      event.setCanceled(true);
   }
}
