package me.pollos.polloshook.impl.module.combat.autototem;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.TotemPopEvent;
import net.minecraft.item.ItemStack;

public class ListenerPop extends ModuleListener<AutoTotem, TotemPopEvent> {
   public ListenerPop(AutoTotem module) {
      super(module, TotemPopEvent.class);
   }

   public void call(TotemPopEvent event) {
      if (mc.player == event.getPlayer()) {
         ((AutoTotem)this.module).serverStack = ItemStack.EMPTY;
      }

   }
}
