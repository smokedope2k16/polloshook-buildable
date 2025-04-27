package me.pollos.polloshook.impl.module.render.customsky;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerStars extends ModuleListener<CustomSky, CustomSky.RenderStarsEvent> {
   public ListenerStars(CustomSky module) {
      super(module, CustomSky.RenderStarsEvent.class);
   }

   public void call(CustomSky.RenderStarsEvent event) {
      if ((Boolean)((CustomSky)this.module).getNoStars().getValue()) {
         event.setCanceled(true);
      }

   }
}
