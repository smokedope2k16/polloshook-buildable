package me.pollos.polloshook.impl.module.player.airplace;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.InteractEvent;
import net.minecraft.util.Hand;

public class ListenerInteract extends ModuleListener<AirPlace, InteractEvent> {
   private boolean weirdFix;

   public ListenerInteract(AirPlace module) {
      super(module, InteractEvent.class);
   }

   public void call(InteractEvent event) {
      if (event.getHand() == Hand.OFF_HAND && this.weirdFix) {
         event.setCanceled(true);
         this.weirdFix = false;
      } else {
         if (((AirPlace)this.module).cancel) {
            event.setCanceled(true);
            ((AirPlace)this.module).cancel = false;
            this.weirdFix = true;
         }

      }
   }
}
