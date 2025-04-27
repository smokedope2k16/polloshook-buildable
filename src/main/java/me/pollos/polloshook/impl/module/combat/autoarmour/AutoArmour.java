package me.pollos.polloshook.impl.module.combat.autoarmour;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.combat.autoarmour.mode.ProtectionMode;
import me.pollos.polloshook.impl.module.combat.autoarmour.util.AutoArmorTimer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmour extends ToggleableModule {
   protected final EnumValue<ProtectionMode> mode;
   protected final Value<Boolean> noBinding;
   protected final Value<Boolean> forceBlastLeggings;
   protected final Value<Boolean> pauseInInv;
   protected final Value<Boolean> allowElytra;
   protected final Value<Boolean> forceElytra;
   public static final byte HELMET_BYTE = 5;
   public static final byte CHEST_BYTE = 6;
   public static final byte LEGGINGS_BYTE = 7;
   public static final byte BOOTS_BYTE = 8;
   protected final AutoArmorTimer timer;

   public AutoArmour() {
      super(new String[]{"AutoArmour", "autoarmor"}, Category.COMBAT);
      this.mode = new EnumValue(ProtectionMode.PROTECTION, new String[]{"Priority", "prio"});
      this.noBinding = new Value(true, new String[]{"NoBinding", "nocursed", "nocurse"});
      this.forceBlastLeggings = new Value(false, new String[]{"ForceBlastLeggings", "blastprio", "blastpriority"});
      this.pauseInInv = new Value(false, new String[]{"PauseInInv", "pauseininventory"});
      this.allowElytra = new Value(true, new String[]{"AllowElytra", "allowely", "elyta"});
      this.forceElytra = (new Value(true, new String[]{"ForceElytra", "swap", "elytraforce"})).setParent(this.allowElytra);
      this.timer = new AutoArmorTimer();
      this.offerValues(new Value[]{this.mode, this.noBinding, this.forceBlastLeggings, this.pauseInInv, this.allowElytra, this.forceElytra});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
   }

   public boolean useElytra() {
      int elytraSlot = -1;
      Item elytraCurrent = null;
      ItemStack byteStack = InventoryUtil.getSlot(6).getStack();
      if (byteStack != null && byteStack.getItem() instanceof ElytraItem) {
         elytraCurrent = byteStack.getItem();
      }

      for(int i = 9; i <= 44; ++i) {
         ItemStack stack = mc.player.currentScreenHandler.getSlot(i).getStack();
         if (stack != null) {
            if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
               break;
            }

            Item item = stack.getItem();
            if (item instanceof ElytraItem && elytraCurrent != item) {
               elytraSlot = i;
               break;
            }
         }
      }

      return this.fastEquip(byteStack, (byte)6, elytraSlot);
   }

   protected boolean fastEquip(ItemStack byteStack, byte elementCodec, int slot) {
      if (slot == -1) {
         return false;
      } else {
         boolean isNull = byteStack == null;
         if (!isNull) {
            this.clickSlot(elementCodec, false);
         }

         this.clickSlot(slot, true);
         if (!isNull) {
            this.clickSlot(slot, false);
         }

         return true;
      }
   }

   private void clickSlot(int slot, boolean shiftClick) {
      mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, shiftClick ? SlotActionType.QUICK_MOVE : SlotActionType.PICKUP, mc.player);
   }

   
   public EnumValue<ProtectionMode> getMode() {
      return this.mode;
   }

   
   public Value<Boolean> getNoBinding() {
      return this.noBinding;
   }

   
   public Value<Boolean> getForceBlastLeggings() {
      return this.forceBlastLeggings;
   }

   
   public Value<Boolean> getPauseInInv() {
      return this.pauseInInv;
   }

   
   public Value<Boolean> getAllowElytra() {
      return this.allowElytra;
   }

   
   public Value<Boolean> getForceElytra() {
      return this.forceElytra;
   }

   
   public AutoArmorTimer getTimer() {
      return this.timer;
   }
}