package me.pollos.polloshook.impl.module.player.choruscontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.ItemStack;

public class ListenerUpdate extends ModuleListener<ChorusControl, UpdateEvent> {
   public ListenerUpdate(ChorusControl module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (!((ChorusControl)this.module).cancel && mc.player.isUsingItem()) {
         ItemStack stack = mc.player.getStackInHand(mc.player.getActiveHand());
         if (stack.getItem() instanceof ChorusFruitItem && stack.getMaxUseTime(mc.player) - mc.player.getItemUseTime() <= 1) {
            ((ChorusControl)this.module).cancel = true;
         }
      }

      if (((ChorusControl)this.module).cancel) {
         ++((ChorusControl)this.module).ticksSinceCancel;
      }

   }
}
