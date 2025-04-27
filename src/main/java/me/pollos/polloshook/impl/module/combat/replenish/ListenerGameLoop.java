package me.pollos.polloshook.impl.module.combat.replenish;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;
import me.pollos.polloshook.impl.module.combat.replenish.mode.ClickMode;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class ListenerGameLoop extends SafeModuleListener<Replenish, GameLoopEvent> {
   public ListenerGameLoop(Replenish module) {
      super(module, GameLoopEvent.class);
   }

   public void safeCall(GameLoopEvent event) {
      if (InventoryUtil.validScreen()) {
         ((Replenish)this.module).observe((ClickMode)((Replenish)this.module).mode.getValue());

         for(int i = 0; i < 9; ++i) {
            ItemStack stack = InventoryUtil.getStack(i);
            if (!stack.isEmpty() && stack.isStackable() && stack.getCount() < (Integer)((Replenish)this.module).threshold.getValue()) {
               if (!((Replenish)this.module).isServerInv && (Boolean)((Replenish)this.module).openInv.getValue()) {
                  PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.OPEN_INVENTORY));
               }

               this.fillStack(stack);
               if (((Replenish)this.module).timer.passed((long)(Integer)((Replenish)this.module).delay.getValue() * 25L)) {
                  ((Replenish)this.module).timer.reset();
               }
            }
         }

      }
   }

   private void fillStack(ItemStack hotbarStack) {
      ((Replenish)this.module).isServerInv = false;
      if (!hotbarStack.isEmpty()) {
         for(int i = 9; i < 36; ++i) {
            ItemStack stack = InventoryUtil.getStack(i);
            if (ItemUtil.equalStack(stack, hotbarStack)) {
               int hotbarSlot = mc.player.getInventory().getSlotWithStack(hotbarStack);
               if (i != hotbarSlot && hotbarSlot != -1 && stack.getCount() > hotbarStack.getCount()) {
                  switch((ClickMode)((Replenish)this.module).mode.getValue()) {
                  case SWAP:
                     if (stack.getCount() + hotbarStack.getCount() > (Integer)((Replenish)this.module).threshold.getValue() * 2) {
                        InventoryUtil.swapClick(i, hotbarSlot);
                     } else {
                        InventoryUtil.quickMove(i);
                     }
                     break;
                  case SHIFT_CLICK:
                     InventoryUtil.quickMove(i);
                  }
               }
            }
         }

      }
   }
}