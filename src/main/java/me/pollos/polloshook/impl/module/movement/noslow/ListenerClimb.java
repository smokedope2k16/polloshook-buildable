package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.movement.noslow.mode.NoSlowClimbingMode;

public class ListenerClimb extends ModuleListener<NoSlow, NoSlow.IsClimbingEvent> {
   public ListenerClimb(NoSlow module) {
      super(module, NoSlow.IsClimbingEvent.class);
   }

   public void call(NoSlow.IsClimbingEvent event) {
      if ((Boolean)((NoSlow)this.module).climbing.getValue() && ((NoSlow)this.module).mode.getValue() == NoSlowClimbingMode.CANCEL) {
         event.setCanceled(true);
      }

   }
}
