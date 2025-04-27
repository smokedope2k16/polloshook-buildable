package me.pollos.polloshook.impl.module.player.fastplace;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.InteractEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class ListenerInteract extends ModuleListener<FastPlace, InteractEvent> {
   private boolean weirdFix;

   public ListenerInteract(FastPlace module) {
      super(module, InteractEvent.class);
   }

   public void call(InteractEvent event) {
      Hand hand = event.getHand();
      ItemStack stack = mc.player.getStackInHand(hand);
      if (hand == Hand.OFF_HAND && this.weirdFix) {
         event.setCanceled(true);
         this.weirdFix = false;
      } else {
         if ((Boolean)((FastPlace)this.module).ghostFix.getValue() && stack.getItem() == Items.END_CRYSTAL) {
            event.setCanceled(true);
            this.weirdFix = true;
         }

      }
   }
}