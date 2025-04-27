package me.pollos.polloshook.impl.module.player.autotool;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.impl.events.block.DamageBlockEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import net.minecraft.util.math.BlockPos;

public class ListenerDamageBlock extends ModuleListener<AutoTool, DamageBlockEvent> {
   public ListenerDamageBlock(AutoTool module) {
      super(module, DamageBlockEvent.class);
   }

   public void call(DamageBlockEvent event) {
      if (!((AutoTool)this.module).sneakCheck()) {
         BlockPos pos = event.getPos();
         if (MineUtil.canBreak(pos) && !mc.player.isCreative() && mc.options.attackKey.isPressed()) {
            int slot = MineUtil.findBestTool(pos);
            if (slot != -1) {
               if (!((AutoTool)this.module).setLastSlot) {
                  ((AutoTool)this.module).lastSlot = mc.player.getInventory().selectedSlot;
                  ((AutoTool)this.module).setLastSlot = true;
               }

               FastBreak FAST_BREAK = (FastBreak)Managers.getModuleManager().get(FastBreak.class);
               boolean valid = FAST_BREAK.isValid(pos);
               if (FAST_BREAK.isEnabled() && !valid) {
                  InventoryUtil.switchToSlot(slot);
               } else if (!FAST_BREAK.isEnabled()) {
                  InventoryUtil.switchToSlot(slot);
               }
            }
         } else if (((AutoTool)this.module).setLastSlot) {
            InventoryUtil.switchToSlot(((AutoTool)this.module).lastSlot);
            ((AutoTool)this.module).reset();
         }

      }
   }
}
