package me.pollos.polloshook.impl.module.movement.tridentfly;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerRain extends ModuleListener<TridentFly, TridentFly.TryUseTridentNoRainEvent> {
   public ListenerRain(TridentFly module) {
      super(module, TridentFly.TryUseTridentNoRainEvent.class);
   }

   public void call(TridentFly.TryUseTridentNoRainEvent event) {
      if ((Boolean)((TridentFly)this.module).alwaysAllow.getValue()) {
         event.setCanceled(true);
      }

   }
}
