package me.pollos.polloshook.impl.module.render.oldpotions;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.render.oldpotions.mode.OldPotionsMode;

public class ListenerOldGlint extends ModuleListener<OldPotions, OldPotions.OldGlintEvent> {
   public ListenerOldGlint(OldPotions module) {
      super(module, OldPotions.OldGlintEvent.class);
   }

   public void call(OldPotions.OldGlintEvent event) {
      if (((OldPotions)this.module).mode.getValue() != OldPotionsMode.COLOR) {
         event.setCanceled(true);
      }
   }
}
