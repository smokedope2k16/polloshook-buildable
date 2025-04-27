package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.render.freecam.mode.FreecamInteractMode;

public class ListenerCrosshair extends ModuleListener<Freecam, Freecam.FindCrosshairEvent> {
   public ListenerCrosshair(Freecam module) {
      super(module, Freecam.FindCrosshairEvent.class);
   }

   public void call(Freecam.FindCrosshairEvent event) {
      if (((Freecam)this.module).interact.getValue() == FreecamInteractMode.PLAYER) {
         event.setEntity(mc.player);
      }

   }
}
