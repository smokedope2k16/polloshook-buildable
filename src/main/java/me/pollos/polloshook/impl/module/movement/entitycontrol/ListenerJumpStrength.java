package me.pollos.polloshook.impl.module.movement.entitycontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerJumpStrength extends ModuleListener<EntityControl, EntityControl.HorseJumpStrengthEvent> {
   public ListenerJumpStrength(EntityControl module) {
      super(module, EntityControl.HorseJumpStrengthEvent.class);
   }

   public void call(EntityControl.HorseJumpStrengthEvent event) {
      if (!(event.getStrength() > (Float)((EntityControl)this.module).jumpStrength.getValue())) {
         event.setStrength((Float)((EntityControl)this.module).jumpStrength.getValue());
         event.setCanceled(true);
      }
   }
}
