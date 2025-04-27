package me.pollos.polloshook.impl.module.movement.tridentfly;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerDelay extends ModuleListener<TridentFly, TridentFly.MaxTridentTicksEvent> {
   public ListenerDelay(TridentFly module) {
      super(module, TridentFly.MaxTridentTicksEvent.class);
   }

   public void call(TridentFly.MaxTridentTicksEvent event) {
      event.setCanceled(true);
   }
}
