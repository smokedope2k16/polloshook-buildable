package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderHandEvent;

public class ListenerHand extends ModuleListener<Freecam, RenderHandEvent> {
   public ListenerHand(Freecam module) {
      super(module, RenderHandEvent.class);
   }

   public void call(RenderHandEvent event) {
      event.setCanceled(true);
   }
}
