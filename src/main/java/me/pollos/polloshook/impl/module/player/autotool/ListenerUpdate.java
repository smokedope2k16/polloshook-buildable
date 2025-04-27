package me.pollos.polloshook.impl.module.player.autotool;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;

public class ListenerUpdate extends ModuleListener<AutoTool, UpdateEvent> {
   public ListenerUpdate(AutoTool module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (!((AutoTool)this.module).sneakCheck()) {
         if (((AutoTool)this.module).setLastSlot && !mc.options.attackKey.isPressed()) {
            InventoryUtil.switchToSlot(((AutoTool)this.module).lastSlot);
            ((AutoTool)this.module).reset();
         }

      }
   }
}
