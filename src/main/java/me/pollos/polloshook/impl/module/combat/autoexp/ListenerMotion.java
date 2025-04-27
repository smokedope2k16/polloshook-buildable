package me.pollos.polloshook.impl.module.combat.autoexp;

import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.combat.autoexp.mode.WasteMatch;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;

public class ListenerMotion extends ModuleListener<AutoExp, MotionUpdateEvent> {
   public ListenerMotion(AutoExp module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if ((Boolean)((AutoExp)this.module).allowInInv.getValue() || !(mc.currentScreen instanceof InventoryScreen) && !(mc.currentScreen instanceof CreativeInventoryScreen)) {
         if (!this.checkWaste()) {
            if (((AutoExp)this.module).timer.passed((double)((Float)((AutoExp)this.module).delay.getValue() * 25.0F)) && event.getStage() == Stage.PRE) {
               int slot = ItemUtil.findHotbarItem(Items.EXPERIENCE_BOTTLE);
               int oldSlot = mc.player.getInventory().selectedSlot;
               if (slot != -1) {
                  if (!ItemUtil.isHolding(Items.EXPERIENCE_BOTTLE)) {
                     InventoryUtil.switchToSlot(slot);
                  }

                  float[] oldRots = RotationsUtil.getMcPlayerRotations();
                  boolean rotateBack = false;
                  if ((Boolean)((AutoExp)this.module).down.getValue()) {
                     PacketUtil.rotate(new float[]{mc.player.getYaw(), 90.0F}, Managers.getPositionManager().isOnGround());
                     rotateBack = true;
                  } else if ((Boolean)((AutoExp)this.module).rotate.getValue()) {
                     PacketUtil.rotate(RotationsUtil.getMcPlayerRotations(), Managers.getPositionManager().isOnGround());
                  }

                  if ((Integer)((AutoExp)this.module).packets.getValue() > 1) {
                     ((AutoExp)this.module).sending = true;
                  }

                  for(int i = 0; i < (Integer)((AutoExp)this.module).packets.getValue(); ++i) {
                     this.sendPacket((Integer)((AutoExp)this.module).packets.getValue() == 1);
                  }

                  PacketUtil.swing();
                  if (rotateBack) {
                     PacketUtil.rotate(oldRots, Managers.getPositionManager().isOnGround());
                  }

                  if ((Boolean)((AutoExp)this.module).silent.getValue()) {
                     InventoryUtil.switchToSlot(oldSlot);
                  }

                  ((AutoExp)this.module).sending = false;
                  ((AutoExp)this.module).timer.reset();
               }
            }

         }
      }
   }

   private boolean checkWaste() {
      if (!(Boolean)((AutoExp)this.module).noWaste.getValue()) {
         return false;
      } else {
         List<Double> percents = new ArrayList();

         for(int i = 3; i >= 0; --i) {
            ItemStack stack = (ItemStack)mc.player.getInventory().armor.get(i);
            if (stack.isEmpty()) {
               return (Boolean)((AutoExp)this.module).onlyIfFullArmor.getValue();
            }

            if (EnchantUtil.getLevel(Enchantments.MENDING, stack) >= 0) {
               percents.add(ItemUtil.getDamageInPercent(stack));
            }
         }

         boolean any = percents.stream().anyMatch((percent) -> {
            return percent > (double)(Integer)((AutoExp)this.module).maxPercent.getValue();
         });
         boolean all = percents.stream().allMatch((percent) -> {
            return percent > (double)(Integer)((AutoExp)this.module).maxPercent.getValue();
         });
         boolean var10000;
         switch((WasteMatch)((AutoExp)this.module).match.getValue()) {
         case ANY:
            var10000 = any;
            break;
         case ALL:
            var10000 = all;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }
   }

   private void sendPacket(boolean actions) {
      if (actions) {
         ((AutoExp)this.module).sending = true;
      }

      ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
         return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
      });
   }
}