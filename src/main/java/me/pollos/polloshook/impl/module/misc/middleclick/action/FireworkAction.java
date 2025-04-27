package me.pollos.polloshook.impl.module.misc.middleclick.action;

import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.module.misc.middleclick.action.actiontype.ActionType;
import me.pollos.polloshook.impl.module.misc.middleclick.action.core.MiddleClickAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult.Type;

public class FireworkAction extends MiddleClickAction {
   public FireworkAction() {
      super(ActionType.FIREWORK);
   }

   public boolean check() {
      if (mc.crosshairTarget != null && mc.player.isFallFlying()) {
         int slot = ItemUtil.getHotbarItemSlot(Items.FIREWORK_ROCKET);
         if (slot == -1) {
            for(int i = 35; i >= 9; --i) {
               ItemStack stack = InventoryUtil.getStack(i);
               if (stack.getItem() == Items.FIREWORK_ROCKET) {
                  slot = i;
               }
            }
         }

         return mc.crosshairTarget.getType() == Type.MISS && slot != -1;
      } else {
         return false;
      }
   }

   public void run() {
      int oldSlot = mc.player.getInventory().selectedSlot;
      int slot = ItemUtil.getHotbarItemSlot(Items.FIREWORK_ROCKET);
      boolean fast = false;
      if (slot == -1) {
         fast = true;

         for(int i = 35; i >= 9; --i) {
            ItemStack stack = InventoryUtil.getStack(i);
            if (stack.getItem() == Items.FIREWORK_ROCKET) {
               slot = i;
            }
         }
      }

      if (slot != -1) {
         if (fast) {
            mc.interactionManager.clickSlot(0, slot, oldSlot, SlotActionType.SWAP, mc.player);
         } else {
            InventoryUtil.switchToSlot(slot);
         }

         ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
            return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
         });
         mc.player.swingHand(Hand.MAIN_HAND);
         if (fast) {
            mc.interactionManager.clickSlot(0, slot, oldSlot, SlotActionType.SWAP, mc.player);
         } else {
            InventoryUtil.switchToSlot(oldSlot);
         }

      }
   }
}