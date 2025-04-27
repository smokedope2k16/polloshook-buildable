package me.pollos.polloshook.impl.module.misc.middleclick.action;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.module.misc.middleclick.action.actiontype.ActionType;
import me.pollos.polloshook.impl.module.misc.middleclick.action.core.MiddleClickAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class PearlAction extends MiddleClickAction {
   public PearlAction() {
      super(ActionType.PEARL);
   }

   public boolean check() {
      int slot = ItemUtil.findHotbarItem(Items.ENDER_PEARL);
      if (slot == -1) {
         return false;
      } else {
         ItemStack stack = InventoryUtil.getStack(slot);
         boolean coolingDown = mc.player.getItemCooldownManager().isCoolingDown(stack.getItem());
         return !coolingDown;
      }
   }

   public void run() {
      int pearlSlot = ItemUtil.getHotbarItemSlot(Items.ENDER_PEARL);
      int lastSlot = mc.player.getInventory().selectedSlot;
      if (pearlSlot != -1) {
         ItemStack pearlStack = mc.player.getStackInHand(Hand.MAIN_HAND);
         if (!mc.player.getItemCooldownManager().isCoolingDown(pearlStack.getItem())) {
            InventoryUtil.switchToSlot(pearlSlot);
            PacketUtil.send(PacketUtil.getRotate(RotationsUtil.getMcPlayerRotations(), Managers.getPositionManager().isOnGround()));
            ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
               return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
            });
            mc.player.swingHand(Hand.MAIN_HAND);
            InventoryUtil.switchToSlot(lastSlot);
         }
      }
   }
}