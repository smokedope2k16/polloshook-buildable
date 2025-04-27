package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.BlockPushEvent;

public class ListenerBlockPush extends ModuleListener<Velocity, BlockPushEvent> {
   public ListenerBlockPush(Velocity module) {
      super(module, BlockPushEvent.class);
   }

   public void call(BlockPushEvent event) {
      if ((Boolean)((Velocity)this.module).noPush.getValue()) {
         event.setCanceled(true);
      }

   }
}
