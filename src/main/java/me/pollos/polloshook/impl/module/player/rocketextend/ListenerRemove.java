package me.pollos.polloshook.impl.module.player.rocketextend;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerRemove extends ModuleListener<FireworkExtend, FireworkExtend.ExplodeAndRemoveEvent> {
   public ListenerRemove(FireworkExtend module) {
      super(module, FireworkExtend.ExplodeAndRemoveEvent.class);
   }

   public void call(FireworkExtend.ExplodeAndRemoveEvent event) {
      ((FireworkExtend)this.module).timer.reset();
   }
}
