package me.pollos.polloshook.impl.module.combat.autoexp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.InteractEvent;

public class ListenerInteract extends ModuleListener<AutoExp, InteractEvent> {
   public ListenerInteract(AutoExp module) {
      super(module, InteractEvent.class);
   }

   public void call(InteractEvent event) {
      if ((Boolean)((AutoExp)this.module).strict.getValue() && ((AutoExp)this.module).sending) {
         event.setCanceled(true);
      }

   }
}
