package me.pollos.polloshook.impl.module.misc.antiinteract;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.block.InteractEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ListenerInteract extends ModuleListener<AntiInteract, InteractEvent> {
   private boolean interact;

   public ListenerInteract(AntiInteract module) {
      super(module, InteractEvent.class);
   }

   public void call(InteractEvent event) {
      BlockPos pos = event.getPos();
      Hand hand = event.getHand();
      if (!mc.player.isSneaking() && !Managers.getPositionManager().isSneaking()) {
         if (hand == Hand.OFF_HAND & this.interact) {
            this.interact = false;
            event.setCanceled(true);
         } else {
            if (((AntiInteract)this.module).isValid(pos, hand)) {
               event.setCanceled(true);
               this.interact = true;
            }

         }
      }
   }
}
