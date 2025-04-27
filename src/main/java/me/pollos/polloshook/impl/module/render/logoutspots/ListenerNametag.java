package me.pollos.polloshook.impl.module.render.logoutspots;

import java.util.ArrayList;
import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderNametagEvent;
import me.pollos.polloshook.impl.module.render.logoutspots.point.LogoutPoint;

public class ListenerNametag extends ModuleListener<LogoutSpots, RenderNametagEvent> {
   public ListenerNametag(LogoutSpots module) {
      super(module, RenderNametagEvent.class);
   }

   public void call(RenderNametagEvent event) {
      Iterator var2 = (new ArrayList(((LogoutSpots)this.module).spots.values())).iterator();

      while(var2.hasNext()) {
         LogoutPoint spot = (LogoutPoint)var2.next();
         if (spot.getPlayer().equals(event.getEntity())) {
            event.setCanceled(true);
         }
      }

   }
}
