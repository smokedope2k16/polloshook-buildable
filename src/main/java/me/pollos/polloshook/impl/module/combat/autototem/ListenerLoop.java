package me.pollos.polloshook.impl.module.combat.autototem;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;
import me.pollos.polloshook.impl.module.combat.autoarmour.AutoArmour;
import me.pollos.polloshook.impl.module.player.suicide.Suicide;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;

public class ListenerLoop extends ModuleListener<AutoTotem, GameLoopEvent> {
   public ListenerLoop(AutoTotem module) {
      super(module, GameLoopEvent.class);
   }

   public void call(GameLoopEvent event) {
      ((AutoTotem)this.module).runningTick = true;
      long time = System.currentTimeMillis();
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!((AutoTotem)this.module).timer.passed(10L)) {
            ((AutoTotem)this.module).runningTick = false;
         } else {
            ((AutoTotem)this.module).serverStack = mc.player.getOffHandStack().copy();
            if (InventoryUtil.validScreen() && !((Suicide)Managers.getModuleManager().get(Suicide.class)).isCrystal()) {
               if ((Boolean)((AutoTotem)this.module).swordGapple.getValue()) {
                  ((AutoTotem)this.module).gap = mc.options.useKey.isPressed() && mc.player.getMainHandStack().getItem() instanceof SwordItem;
                  if (((AutoTotem)this.module).gap) {
                     ((AutoTotem)this.module).gapTimer.reset();
                  }
               }

               if (!((AutoTotem)this.module).gapTimer.passed(125L)) {
                  ((AutoTotem)this.module).gap = true;
               }

               Item item = ((AutoTotem)this.module).getItem(((AutoTotem)this.module).gap);
               ((AutoTotem)this.module).currentItem = item;
               ItemStack offHandStack = ((AutoTotem)this.module).serverStack;
               if (this.isItem(offHandStack.getItem(), item)) {
                  ((AutoTotem)this.module).runningTick = false;
                  return;
               }

               ItemStack cursorStack = mc.player.currentScreenHandler.getCursorStack();
               if (cursorStack.getItem().equals(item)) {
                  if ((Boolean)((AutoTotem)this.module).debug.getValue()) {
                     ClientLogger.getLogger().log(String.valueOf(Formatting.AQUA) + "<AutoTotem> Cursor swap", false);
                  }

                  ((AutoTotem)this.module).serverStack = item.getDefaultStack();
                  mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);
                  ((AutoTotem)this.module).timer.reset();
                  ((AutoTotem)this.module).runningTick = false;
                  ((AutoTotem)this.module).lastAction = System.currentTimeMillis() - time;
                  return;
               }

               int itemSlot = ((AutoTotem)this.module).getSlot(item);
               AutoArmour AUTO_ARMOUR_MODULE = (AutoArmour)Managers.getModuleManager().get(AutoArmour.class);
               if (itemSlot != -1) {
                  ((AutoTotem)this.module).serverStack = item.getDefaultStack();
                  ((AutoTotem)this.module).timer.reset();
                  int slot = InventoryUtil.fixItemSlot(itemSlot);
                  if ((Boolean)((AutoTotem)this.module).oneDotTwelve.getValue() || !offHandStack.isEmpty() && !(Boolean)((AutoTotem)this.module).alwaysQuickSwap.getValue()) {
                     if ((Boolean)((AutoTotem)this.module).debug.getValue()) {
                        ClientLogger.getLogger().log(String.valueOf(Formatting.BLUE) + "<AutoTotem> Normal swap", false);
                     }

                     mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.PICKUP, mc.player);
                     mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player);
                     if (!offHandStack.isEmpty()) {
                        mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.PICKUP, mc.player);
                     }
                  } else {
                     if ((Boolean)((AutoTotem)this.module).debug.getValue()) {
                        ClientLogger.getLogger().log(String.valueOf(Formatting.BLUE) + "<AutoTotem> Quick swap", false);
                     }

                     mc.interactionManager.clickSlot(0, slot, 40, SlotActionType.SWAP, mc.player);
                  }

                  ((AutoTotem)this.module).lastAction = System.currentTimeMillis() - time;
                  if (AUTO_ARMOUR_MODULE.isEnabled()) {
                     AUTO_ARMOUR_MODULE.getTimer().getAutoTotemTimer().reset();
                  }
               }
            }

            ((AutoTotem)this.module).runningTick = false;
         }
      } else {
         ((AutoTotem)this.module).runningTick = false;
      }
   }

   private boolean isItem(Item item1, Item item2) {
      return item1.equals(item2) || Item.getRawId(item1) == Item.getRawId(item2) || item1.getName().equals(item2.getName());
   }
}