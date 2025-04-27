package me.pollos.polloshook.impl.module.player.fakeplayer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.DeathEvent;

public class ListenerDeath extends ModuleListener<FakePlayer, DeathEvent> {
   public ListenerDeath(FakePlayer module) {
      super(module, DeathEvent.class);
   }

   public void call(DeathEvent event) {
      if (event.getEntity() == mc.player) {
         ((FakePlayer)this.module).setEnabled(false);
      }

   }
}
