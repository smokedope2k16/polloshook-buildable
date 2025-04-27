package me.pollos.polloshook.impl.module.render.oldpotions;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.render.oldpotions.mode.OldPotionsMode;

public class ListenerOldColor extends ModuleListener<OldPotions, OldPotions.OldColorEvent> {
   public ListenerOldColor(OldPotions module) {
      super(module, OldPotions.OldColorEvent.class);
   }

   public void call(OldPotions.OldColorEvent event) {
      if (((OldPotions)this.module).mode.getValue() != OldPotionsMode.GLINT) {
         event.setCanceled(true);
      }
   }
}
