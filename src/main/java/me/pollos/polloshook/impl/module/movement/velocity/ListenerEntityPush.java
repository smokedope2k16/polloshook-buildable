package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.EntityPushEvent;

public class ListenerEntityPush extends ModuleListener<Velocity, EntityPushEvent> {
   public ListenerEntityPush(Velocity module) {
      super(module, EntityPushEvent.class);
   }

   public void call(EntityPushEvent event) {
      if ((Boolean)((Velocity)this.module).noPush.getValue()) {
         event.setCanceled(true);
      }

   }
}
