package me.pollos.polloshook.impl.command.player;

import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class ChestSwapCommand extends Command {
   public ChestSwapCommand() {
      super(new String[]{"ChestSwap", "cycleelytra"});
   }

   public String execute(String[] args) {
      boolean isElytra = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
      boolean empty = mc.player.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
      int slot = -1;
      ItemStack stack;
      Item item;
      if (isElytra) {
         int currentProtection = -1;
         Item current = null;

         for(byte i = 9; i <= 44; ++i) {
            stack = InventoryUtil.getSlot(i).getStack();
            if (stack != null) {
               item = stack.getItem();
               if (item instanceof ArmorItem) {
                  ArmorItem armor = (ArmorItem)item;
                  if (EnchantUtil.getLevel(Enchantments.BINDING_CURSE, stack) <= 0) {
                     int armorProtection = armor.getProtection() + EnchantUtil.getLevel(Enchantments.PROTECTION, stack);
                     if (armor.getSlotType() == EquipmentSlot.CHEST && (current == null || currentProtection < armorProtection) && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != item) {
                        currentProtection = armorProtection;
                        current = item;
                        slot = i;
                     }
                  }
               }
            }
         }
      } else {
         Item elytraCurrent = null;
         ItemStack byteStack = InventoryUtil.getSlot(6).getStack();
         if (byteStack != null && byteStack.getItem() instanceof ElytraItem) {
            elytraCurrent = byteStack.getItem();
         }

         for(int i = 9; i <= 44; ++i) {
            stack = mc.player.currentScreenHandler.getSlot(i).getStack();
            if (stack != null) {
               if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                  break;
               }

               item = stack.getItem();
               if (item instanceof ElytraItem && elytraCurrent != item) {
                  slot = i;
                  break;
               }
            }
         }
      }

      if (slot == -1) {
         return "no " + (isElytra ? "chestplates found" : "elytras found");
      } else {
         if (!empty) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, mc.player);
         }

         mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
         if (!empty) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.PICKUP, mc.player);
         }

         return "swapping to " + (isElytra ? "chestplate" : "elytra");
      }
   }
}