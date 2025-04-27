package me.pollos.polloshook.impl.module.player.yaw;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;

public class ListenerTurnHead extends ModuleListener<YawLock, Freecam.EntityTurnHeadEvent> {
   public ListenerTurnHead(YawLock module) {
      super(module, Freecam.EntityTurnHeadEvent.class);
   }

   public void call(Freecam.EntityTurnHeadEvent event) {
      if (event.getEntity() == mc.player) {
         event.setLockYaw((Boolean)((YawLock)this.module).noCameraTurn.getValue());
         event.setCanceled(true);
      } else {
         event.setLockYaw(false);
      }

   }
}
