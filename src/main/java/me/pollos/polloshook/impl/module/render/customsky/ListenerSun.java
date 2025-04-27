package me.pollos.polloshook.impl.module.render.customsky;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerSun extends ModuleListener<CustomSky, CustomSky.RenderSunOrMoonEvent> {
   public ListenerSun(CustomSky module) {
      super(module, CustomSky.RenderSunOrMoonEvent.class);
   }

   public void call(CustomSky.RenderSunOrMoonEvent event) {
      if ((Boolean)((CustomSky)this.module).getNoSunMoon().getValue()) {
         event.setCanceled(true);
      }

   }
}
