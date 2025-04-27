package me.pollos.polloshook.impl.module.render.crosshair;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerRenderCrosshair extends ModuleListener<Crosshair, Crosshair.RenderCrosshairEvent> {
   public ListenerRenderCrosshair(Crosshair module) {
      super(module, Crosshair.RenderCrosshairEvent.class);
   }

   public void call(Crosshair.RenderCrosshairEvent event) {
      event.setCanceled(true);
   }
}
