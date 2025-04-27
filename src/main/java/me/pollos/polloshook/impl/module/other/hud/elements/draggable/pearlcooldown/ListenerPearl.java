package me.pollos.polloshook.impl.module.other.hud.elements.draggable.pearlcooldown;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.PearlThrowEvent;

public class ListenerPearl extends ModuleListener<PearlCooldown, PearlThrowEvent> {
   public ListenerPearl(PearlCooldown module) {
      super(module, PearlThrowEvent.class);
   }

   public void call(PearlThrowEvent event) {
      if (event.getThrower() == mc.player) {
         ((PearlCooldown)this.module).timer.reset();
      } else {
         if ((Boolean)((PearlCooldown)this.module).displayOthers.getValue()) {
            ((PearlCooldown)this.module).otherPlayers.put(event.getThrower(), System.currentTimeMillis());
         }

      }
   }
}
