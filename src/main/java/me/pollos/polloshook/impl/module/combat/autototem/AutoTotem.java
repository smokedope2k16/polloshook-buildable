package me.pollos.polloshook.impl.module.combat.autototem;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.combat.autototem.mode.AutoTotemItem;
import me.pollos.polloshook.impl.module.combat.autototem.mode.TotemInfo;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;

public class AutoTotem extends ToggleableModule {
   protected final EnumValue<AutoTotemItem> itemMode;
   protected final NumberValue<Integer> gapSlot;
   protected final Value<Boolean> alwaysQuickSwap;
   protected final NumberValue<Float> health;
   protected final NumberValue<Float> holeHealth;
   protected final Value<Boolean> estrogen;
   protected final Value<Boolean> strictEstrogen;
   protected final NumberValue<Float> minEstrogen;
   protected final Value<Boolean> swordGapple;
   protected final Value<Boolean> gappleOverride;
   protected final Value<Boolean> crystalPick;
   protected final Value<Boolean> superSafe;
   protected final Value<Boolean> oneDotTwelve;
   protected final Value<Boolean> debug;
   protected final Value<Boolean> deathVerbose;
   protected final EnumValue<TotemInfo> info;
   protected final StopWatch timer;
   protected final StopWatch gapTimer;
   protected boolean gap;
   protected boolean runningTick;
   protected long lastAction;
   protected Item currentItem;
   protected ItemStack serverStack;

   public AutoTotem() {
      super(new String[]{"AutoTotem", "totem", "offhand"}, Category.COMBAT);
      this.itemMode = new EnumValue(AutoTotemItem.TOTEMS, new String[]{"Item", "i"});
      this.gapSlot = new NumberValue(1, 1, 9, new String[]{"GapSlot", "antigap"});
      this.alwaysQuickSwap = new Value(false, new String[]{"AlwaysQuickSwap", "quickswap", "quick"});
      this.health = new NumberValue(16.0F, 1.0F, 20.0F, 0.5F, new String[]{"Health"});
      this.holeHealth = new NumberValue(8.0F, 1.0F, 20.0F, 0.5F, new String[]{"HoleHealth", "holehp"});
      this.estrogen = new Value(false, new String[]{"Absortion", "abostroinss", "estrogen"});
      this.strictEstrogen = (new Value(false, new String[]{"StrictAbsortion", "strictestrogen"})).setParent(this.estrogen);
      this.minEstrogen = (new NumberValue(6.0F, 1.0F, 16.0F, 0.5F, new String[]{"MinAbsortion", "absortionamount"})).setParent(this.estrogen);
      this.swordGapple = new Value(false, new String[]{"SwordGapple", "swordgap"});
      this.gappleOverride = (new Value(false, new String[]{"GappleOverride", "overridehealth"})).setParent(this.swordGapple);
      this.crystalPick = new Value(false, new String[]{"PickCrystal", "crystalpick"});
      this.superSafe = new Value(false, new String[]{"SuperSafe", "safe"});
      this.oneDotTwelve = new Value(false, new String[]{"1.12", "onedottwelve"});
      this.debug = new Value(false, new String[]{"Debug", "debugger"});
      this.deathVerbose = new Value(false, new String[]{"DeathVerbose", "deaththing"});
      this.info = new EnumValue(TotemInfo.COUNT, new String[]{"Info", "informations"});
      this.timer = new StopWatch();
      this.gapTimer = new StopWatch();
      this.runningTick = false;
      this.lastAction = 0L;
      this.currentItem = Items.TOTEM_OF_UNDYING;
      this.serverStack = ItemStack.EMPTY;
      this.offerValues(new Value[]{this.itemMode, this.gapSlot, this.alwaysQuickSwap, this.health, this.holeHealth, this.estrogen, this.strictEstrogen, this.minEstrogen, this.swordGapple, this.gappleOverride, this.crystalPick, this.superSafe, this.oneDotTwelve, this.debug, this.deathVerbose, this.info});
      this.offerListeners(new Listener[]{new ListenerLoop(this), new ListenerPop(this), new ListenerInteract(this), new ListenerDeath(this)});
   }

   protected void onToggle() {
      this.gap = false;
      this.currentItem = Items.TOTEM_OF_UNDYING;
      if (mc.player != null) {
         this.serverStack = mc.player.getOffHandStack();
      }

   }

   public String getDisplayLabel() {
      return mc.player == null ? "AutoTotem" : this.getInfoLabel();
   }

   protected String getTag() {
      return mc.player == null ? null : this.getTagLabel();
   }

   protected int getSlot(Item item) {
      int i;
      ItemStack stack;
      for(i = 35; i >= 9; --i) {
         stack = InventoryUtil.getStack(i);
         if (stack.getItem() == item) {
            return i;
         }
      }

      for(i = 8; i >= 0; --i) {
         stack = InventoryUtil.getStack(i);
         if (stack.getItem() == item && (Integer)this.gapSlot.getValue() - 1 != i) {
            return i;
         }
      }

      return -1;
   }

   protected Item getItem(boolean gap) {
      Item item = Items.TOTEM_OF_UNDYING;
      if (mc.player == null) {
         return item;
      } else if (!(Boolean)this.superSafe.getValue() || !(mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem) && !(mc.player.getMainHandStack().getItem() instanceof ElytraItem)) {
         if ((Boolean)this.superSafe.getValue() && this.armorCheck() && !gap) {
            return item;
         } else if ((Boolean)this.superSafe.getValue() && mc.player.fallDistance > 10.0F) {
            return item;
         } else if ((Boolean)this.superSafe.getValue() && !Managers.getSafeManager().isSafe()) {
            return item;
         } else {
            if ((Boolean)this.estrogen.getValue()) {
               if ((Boolean)this.strictEstrogen.getValue()) {
                  if (mc.player.getAbsorptionAmount() < (Float)this.minEstrogen.getValue()) {
                     return item;
                  }
               } else if (mc.player.getAbsorptionAmount() < (Float)this.minEstrogen.getValue() && mc.player.getHealth() < (Float)this.health.getValue()) {
                  return item;
               }
            }

            boolean inHole = EntityUtil.isSafe(mc.player);
            if (EntityUtil.getHealth(mc.player) >= this.getHealth(inHole, gap, (Boolean)this.gappleOverride.getValue())) {
               if ((Boolean)this.crystalPick.getValue() && mc.player.getMainHandStack().getItem() instanceof PickaxeItem) {
                  item = Items.END_CRYSTAL;
               } else if (gap) {
                  item = Items.ENCHANTED_GOLDEN_APPLE;
               } else {
                  item = ((AutoTotemItem)this.itemMode.getValue()).getItem();
               }
            }

            return item;
         }
      } else {
         return item;
      }
   }

   protected float getHealth(boolean safe, boolean gapple, boolean antigap) {
      if (gapple) {
         return antigap ? 0.0F : safe ? (Float)this.holeHealth.getValue() : (Float)this.health.getValue();
      } else {
         return safe ? (Float)this.holeHealth.getValue() : (Float)this.health.getValue();
      }
   }

   private String getInfoLabel() {
      String label = "AutoTotem";
      switch((TotemInfo)this.info.getValue()) {
      case OFFHAND:
         label = this.currentItem != Items.TOTEM_OF_UNDYING ? "Offhand" : label;
         break;
      case COUNT:
      case SWIFT:
         label = this.currentItem == Items.END_CRYSTAL ? "OffhandCrystal" : (this.currentItem == Items.ENCHANTED_GOLDEN_APPLE ? "OffhandGapple" : label);
      }

      return label;
   }

private String getTagLabel() {
    String label = String.valueOf(InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING));

    switch ((TotemInfo) this.info.getValue()) {
        case OFFHAND:
            if (this.currentItem == Items.END_CRYSTAL) {
                label = "Crystal";
            } else if (this.currentItem == Items.ENCHANTED_GOLDEN_APPLE) {
                label = "Gapple";
            }
            break;
        case COUNT:
            label = String.valueOf(InventoryUtil.getItemCount(this.currentItem));
            break;
        case SWIFT:
            if (this.currentItem != Items.TOTEM_OF_UNDYING) {
                label = null; 
            }
            break;
    }

    return label;
}


   private boolean armorCheck() {
      for(int i = 3; i >= 0; --i) {
         ItemStack stack = (ItemStack)mc.player.getInventory().armor.get(i);
         if (stack.isEmpty()) {
            return true;
         }

         if (ItemUtil.getDamageInPercent(stack) < 10.0D) {
            return true;
         }
      }

      return false;
   }
}