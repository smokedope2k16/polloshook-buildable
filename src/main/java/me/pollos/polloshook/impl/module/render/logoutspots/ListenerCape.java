package me.pollos.polloshook.impl.module.render.logoutspots;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderCapeEvent;

public class ListenerCape extends ModuleListener<LogoutSpots, RenderCapeEvent> {
   public ListenerCape(LogoutSpots module) {
      super(module, RenderCapeEvent.class);
   }

   public void call(RenderCapeEvent event) {
      ((LogoutSpots)this.module).spots.forEach((uuid, logoutPoint) -> {
         if (event.getPlayer() == logoutPoint.getPlayer()) {
            event.setCanceled(true);
         }

      });
   }
}
