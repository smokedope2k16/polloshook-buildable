package me.pollos.polloshook.impl.module.player.autotool;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.DeathEvent;

public class ListenerDeath extends ModuleListener<AutoTool, DeathEvent> {
   public ListenerDeath(AutoTool module) {
      super(module, DeathEvent.class);
   }

   public void call(DeathEvent event) {
      if (event.getEntity() == mc.player) {
         ((AutoTool)this.module).reset();
      }

   }
}
