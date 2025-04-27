package me.pollos.polloshook.impl.module.combat.autoarmour;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.combat.autoarmour.mode.ProtectionMode;
import me.pollos.polloshook.impl.module.combat.autoarmour.util.QuadTimer;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ListenerTick extends SafeModuleListener<AutoArmour, TickEvent> {
   public ListenerTick(AutoArmour module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (InventoryUtil.validScreen()) {
         if (!(Boolean)((AutoArmour)this.module).pauseInInv.getValue() || !(mc.currentScreen instanceof InventoryScreen)) {
            float factor = (20.0F - Managers.getTpsManager().getCurrentTps()) * 100.0F;

            for(byte elementCodec = 5; elementCodec <= 8; ++elementCodec) {
               StopWatch timer = this.getTimerFromByte(elementCodec);
               if (timer != null && timer.passed((double)(factor + 250.0F)) && ((AutoArmour)this.module).timer.getAutoArmorTimer().passed((double)(factor + 50.0F)) && this.equipArmor(elementCodec)) {
                  timer.reset();
                  break;
               }
            }

         }
      }
   }

   private StopWatch getTimerFromByte(byte elementCodec) {
      QuadTimer timer = ((AutoArmour)this.module).getTimer().getAutoArmorTimer();
      switch(elementCodec) {
      case 5:
         return timer.getTimer1();
      case 6:
         return timer.getTimer2();
      case 7:
         return timer.getTimer3();
      case 8:
         return timer.getTimer4();
      default:
         return null;
      }
   }

   private boolean equipArmor(byte elementCodec) {
      if ((Boolean)((AutoArmour)this.module).allowElytra.getValue() && (Boolean)((AutoArmour)this.module).forceElytra.getValue() && elementCodec == 6 && ((AutoArmour)this.module).useElytra()) {
         return false;
      } else {
         int currentProtection = -1;
         int slot = -1;
         Item current = null;
         ItemStack byteStack = InventoryUtil.getSlot(elementCodec).getStack();
         if (byteStack != null && (byteStack.getItem() instanceof ArmorItem || (Boolean)((AutoArmour)this.module).allowElytra.getValue() && byteStack.getItem() instanceof ElytraItem)) {
            current = byteStack.getItem();
            if (current instanceof ArmorItem) {
               currentProtection = ((ArmorItem)current).getProtection() + this.getProtection(byteStack, elementCodec);
            }
         }

         boolean elytraEquipped = current instanceof ElytraItem;
         if (elytraEquipped) {
            return false;
         } else {
            for(byte i = 9; i <= 44; ++i) {
               ItemStack stack = InventoryUtil.getSlot(i).getStack();
               if (stack != null) {
                  Item item = stack.getItem();
                  if ((item instanceof ArmorItem || (Boolean)((AutoArmour)this.module).allowElytra.getValue() && item instanceof ElytraItem) && (!(Boolean)((AutoArmour)this.module).noBinding.getValue() || EnchantUtil.getLevel(Enchantments.BINDING_CURSE, stack) <= 0)) {
                     int armorProtection = 0;
                     if (item instanceof ArmorItem) {
                        ArmorItem armor = (ArmorItem)item;
                        armorProtection = armor.getProtection() + this.getProtection(stack, elementCodec);
                     }

                     if (this.checkArmor(item, elementCodec) && (current == null || currentProtection < armorProtection) && !(item instanceof ElytraItem) && !this.doSkipArmor(elementCodec, item)) {
                        currentProtection = armorProtection;
                        current = item;
                        slot = i;
                     }
                  }
               }
            }

            return ((AutoArmour)this.module).fastEquip(byteStack, elementCodec, slot);
         }
      }
   }

   private int getProtection(ItemStack stack, byte elementCodec) {
      boolean doForceLeggings = (Boolean)((AutoArmour)this.module).forceBlastLeggings.getValue() && elementCodec == 7;
      return EnchantUtil.getLevel(doForceLeggings ? Enchantments.BLAST_PROTECTION : ((ProtectionMode)((AutoArmour)this.module).getMode().getValue()).getEnchant(), stack);
   }

   private boolean doSkipArmor(byte elementCodec, Item item) {
      boolean var10000;
      switch(elementCodec) {
      case 5:
         var10000 = mc.player.getEquippedStack(EquipmentSlot.HEAD).getItem() == item;
         break;
      case 6:
         var10000 = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == item;
         break;
      case 7:
         var10000 = mc.player.getEquippedStack(EquipmentSlot.LEGS).getItem() == item;
         break;
      case 8:
         var10000 = mc.player.getEquippedStack(EquipmentSlot.FEET).getItem() == item;
         break;
      default:
         var10000 = false;
      }

      return var10000;
   }

   private boolean checkArmor(Item item, byte elementCodec) {
      if (!(item instanceof ArmorItem)) {
         return item instanceof ElytraItem;
      } else {
         ArmorItem armorItem = (ArmorItem)item;
         return elementCodec == 5 && armorItem.getSlotType() == EquipmentSlot.HEAD || elementCodec == 6 && armorItem.getSlotType() == EquipmentSlot.CHEST || elementCodec == 7 && armorItem.getSlotType() == EquipmentSlot.LEGS || elementCodec == 8 && armorItem.getSlotType() == EquipmentSlot.FEET;
      }
   }
}