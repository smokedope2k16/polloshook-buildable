package me.pollos.polloshook.impl.module.movement.boatfly;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerGetGravity extends ModuleListener<BoatFly, BoatFly.GetGravityEvent> {
   public ListenerGetGravity(BoatFly module) {
      super(module, BoatFly.GetGravityEvent.class);
   }

   public void call(BoatFly.GetGravityEvent event) {
      if (((BoatFly)this.module).isValid(event.getEntity())) {
         if ((Float)((BoatFly)this.module).downSpeed.getValue() != 0.0F) {
            event.setCanceled(true);
         }
      }
   }
}
