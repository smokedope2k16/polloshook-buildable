package me.pollos.polloshook.impl.module.player.sprint;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.movement.SetSprintEvent;
import me.pollos.polloshook.impl.module.player.sprint.mode.SprintMode;

public class ListenerSprint extends SafeModuleListener<Sprint, SetSprintEvent> {
   public ListenerSprint(Sprint module) {
      super(module, SetSprintEvent.class);
   }

   public void safeCall(SetSprintEvent event) {
      if (!((Sprint)this.module).doReturn()) {
         if (((Sprint)this.module).canSprint() && ((Sprint)this.module).mode.getValue() == SprintMode.RAGE) {
            event.setCanceled(true);
         }

      }
   }
}
