package me.pollos.polloshook.impl.module.player.rocketextend;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;

public class ListenerUpdate extends ModuleListener<FireworkExtend, UpdateEvent> {
   public ListenerUpdate(FireworkExtend module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (((FireworkExtend)this.module).rocket == null) {
         ((FireworkExtend)this.module).timer.reset();
      } else {
         if (!mc.player.isFallFlying() || mc.player.isOnGround()) {
            ((FireworkExtend)this.module).removeRocket();
         }

      }
   }
}
