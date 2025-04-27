package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.movement.KeepSprintEvent;

public class ListenerKeepSprint extends ModuleListener<NoSlow, KeepSprintEvent> {
   public ListenerKeepSprint(NoSlow module) {
      super(module, KeepSprintEvent.class);
   }

   public void call(KeepSprintEvent event) {
      if ((Boolean)((NoSlow)this.module).sprint.getValue()) {
         event.setMotion(mc.player.getVelocity().multiply(1.0D));
         event.setCanceled(true);
      }

   }
}
