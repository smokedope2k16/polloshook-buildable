package me.pollos.polloshook.impl.module.movement.tridentfly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.api.minecraft.world.EnchantUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ListenerMotion extends ModuleListener<TridentFly, MotionUpdateEvent> {
   public ListenerMotion(TridentFly module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      ItemStack stack = ItemUtil.getHeldItemStack(Items.TRIDENT);
      boolean trident = ItemUtil.isHolding(Items.TRIDENT) && EnchantUtil.getLevel(Enchantments.RIPTIDE, ItemUtil.getHeldItemStack(Items.TRIDENT)) >= 1;
      boolean useFlag = !MovementUtil.anyMovementKeysWASD() && (Boolean)((TridentFly)this.module).requireInput.getValue() || !mc.options.useKey.isPressed() && (Boolean)((TridentFly)this.module).requireMouseDown.getValue();
      if ((Boolean)((TridentFly)this.module).pitchLock.getValue() && trident && !useFlag) {
         Managers.getRotationManager().setRotations(event.getYaw(), (float)(Integer)((TridentFly)this.module).pitch.getValue(), event);
      }

      if (((TridentFly)this.module).timer.passed((double)((Float)((TridentFly)this.module).delay.getValue() * 125.0F)) && trident && !useFlag && !(Managers.getSpeedManager().getSpeed(mc.player) / 100.0D > (double)(Integer)((TridentFly)this.module).maxSpeed.getValue()) && stack.getItem() instanceof TridentItem) {
         if (!(Boolean)((TridentFly)this.module).requireMouseDown.getValue()) {
            mc.options.useKey.setPressed(((TridentFly)this.module).flag = true);
         }

         if ((Boolean)((TridentFly)this.module).autoRelease.getValue() && mc.player.getItemUseTime() >= (Integer)((TridentFly)this.module).useTicks.getValue()) {
            PacketUtil.send(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            mc.player.stopUsingItem();
         }

      }
   }
}