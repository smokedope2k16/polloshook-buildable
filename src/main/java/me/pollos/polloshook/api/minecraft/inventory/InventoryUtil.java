package me.pollos.polloshook.api.minecraft.inventory;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

public class InventoryUtil implements Minecraftable {
   public static void syncItem() {
      ((IClientPlayerInteractionManager)mc.interactionManager).syncItem();
   }

   public static void switchToSlot(int slot) {
      if (mc.player.getInventory().selectedSlot != slot && slot > -1 && slot < 9) {
         mc.player.getInventory().selectedSlot = slot;
         syncItem();
      }

   }

   public static void altSwap(int slot) {
      slot = hotbarToInventory(slot);
      if (mc.player.getInventory().selectedSlot != slot && slot > 35 && slot < 45) {
         mc.interactionManager.clickSlot(0, slot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
      }

   }

   public static int hotbarToInventory(int slot) {
      if (slot == -2) {
         return 45;
      } else {
         return slot > -1 && slot < 9 ? 36 + slot : slot;
      }
   }

   public static int getItemCount(Item item) {
      int count = 0;
      int size = mc.player.getInventory().main.size();

      for(int i = 0; i < size; ++i) {
         ItemStack itemStack = (ItemStack)mc.player.getInventory().main.get(i);
         if (itemStack.getItem() == item) {
            count += itemStack.getCount();
         }
      }

      ItemStack offhandStack = mc.player.getOffHandStack();
      if (offhandStack.getItem() == item) {
         count += offhandStack.getCount();
      }

      return count;
   }

   public static void pickupClick(int to) {
      mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, to, 0, SlotActionType.PICKUP, mc.player);
   }

   public static void swapClick(int from, int to) {
      mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, from, to, SlotActionType.SWAP, mc.player);
   }

   public static void quickMove(int to) {
      mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, to, 0, SlotActionType.QUICK_MOVE, mc.player);
   }

   public static ItemStack getStack(int slot) {
      return slot == -2 ? mc.player.getOffHandStack() : mc.player.getInventory().getStack(slot);
   }

   public static int fixItemSlot(int i) {
      return i < 9 ? i + 36 : i;
   }

   public static Slot getSlot(int slot) {
      return mc.player.currentScreenHandler.getSlot(slot);
   }

   public static boolean validScreen() {
      return mc.player.playerScreenHandler == mc.player.currentScreenHandler;
   }

   public static void drop(int slot) {
      mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.THROW, mc.player);
   }
}
