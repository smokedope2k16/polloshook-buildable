package me.pollos.polloshook.impl.module.player.scaffold;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.BlockPushEvent;

public class ListenerPush extends ModuleListener<Scaffold, BlockPushEvent> {
   public ListenerPush(Scaffold module) {
      super(module, BlockPushEvent.class);
   }

   public void call(BlockPushEvent event) {
      event.setCanceled((Boolean)((Scaffold)this.module).tower.getValue());
   }
}
