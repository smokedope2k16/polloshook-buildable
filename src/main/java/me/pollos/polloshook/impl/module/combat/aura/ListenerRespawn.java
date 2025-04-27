package me.pollos.polloshook.impl.module.combat.aura;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.RespawnEvent;

public class ListenerRespawn extends ModuleListener<Aura, RespawnEvent> {
   public ListenerRespawn(Aura module) {
      super(module, RespawnEvent.class);
   }

   public void call(RespawnEvent event) {
      if ((Boolean)((Aura)this.module).respawnDisable.getValue()) {
         ((Aura)this.module).toggle();
      }

   }
}
