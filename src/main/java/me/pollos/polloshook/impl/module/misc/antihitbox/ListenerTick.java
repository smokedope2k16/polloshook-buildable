package me.pollos.polloshook.impl.module.misc.antihitbox;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ListenerTick extends SafeModuleListener<AntiHitbox, TickEvent> {
   public ListenerTick(AntiHitbox module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      ItemStack stack = mc.player.getMainHandStack();
      Item item = stack.getItem();
      ((AntiHitbox)this.module).isValid = ((AntiHitbox)this.module).items.isValid(item, ((AntiHitbox)this.module).selection);
   }
}
