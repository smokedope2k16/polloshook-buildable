package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.CaveCullingEvent;

public class ListenerCaveCulling extends ModuleListener<Freecam, CaveCullingEvent> {
   public ListenerCaveCulling(Freecam module) {
      super(module, CaveCullingEvent.class);
   }

   public void call(CaveCullingEvent event) {
      event.setCanceled(true);
   }
}
