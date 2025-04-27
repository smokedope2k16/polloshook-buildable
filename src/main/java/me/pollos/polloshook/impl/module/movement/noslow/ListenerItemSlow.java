package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.item.InputUpdateEvent;
import net.minecraft.client.input.Input;

public class ListenerItemSlow extends ModuleListener<NoSlow, InputUpdateEvent> {
   public ListenerItemSlow(NoSlow module) {
      super(module, InputUpdateEvent.class);
   }

   public void call(InputUpdateEvent event) {
      if ((Boolean)((NoSlow)this.module).items.getValue() && mc.player.isUsingItem() && !mc.player.hasVehicle()) {
         Input var10000 = mc.player.input;
         var10000.movementSideways /= 0.2F;
         var10000 = mc.player.input;
         var10000.movementForward /= 0.2F;
      }

   }
}
