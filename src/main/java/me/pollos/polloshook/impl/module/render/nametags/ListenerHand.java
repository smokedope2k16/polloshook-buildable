package me.pollos.polloshook.impl.module.render.nametags;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderHandEvent;

public class ListenerHand extends ModuleListener<Nametags, RenderHandEvent> {
   public ListenerHand(Nametags module) {
      super(module, RenderHandEvent.class);
   }

   public void call(RenderHandEvent event) {
      if (!((Nametags)this.module).ignore) {
         event.setCanceled(true);
      }

   }
}
