package me.pollos.polloshook.impl.module.movement.elytrafly;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.inventory.ItemUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.elytrafly.mode.ElytraFlyMode;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ListenerMove extends SafeModuleListener<ElytraFly, MoveEvent> {
   public ListenerMove(ElytraFly module) {
      super(module, MoveEvent.class);
   }

   public void safeCall(MoveEvent event) {
      if (!mc.player.isOnGround() && !mc.player.isFallFlying() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA && mc.player.input.jumping && (Boolean)((ElytraFly)this.module).autoStart.getValue()) {
         if (((ElytraFly)this.module).startTimer.passed(350L)) {
            Managers.getTimerManager().setYieldTimer(true);
            Managers.getTimerManager().set(0.17F);
            Managers.getTimerManager().setYieldTimer(false);
            mc.player.checkFallFlying();
            PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            ((ElytraFly)this.module).startTimer.reset();
         }
      } else {
         Managers.getTimerManager().reset();
      }

      switch((ElytraFlyMode)((ElytraFly)this.module).mode.getValue()) {
      case CONTROL:
         double[] dir = MovementUtil.strafe((double)((Float)((ElytraFly)this.module).hSpeed.getValue() / 10.0F));
         boolean verticalCheck = !(Boolean)((ElytraFly)this.module).vertically.getValue() && (mc.player.input.jumping || mc.player.input.sneaking);
         if (((ElytraFly)this.module).isElytra() && !verticalCheck) {
            double vertical = (double)((Float)((ElytraFly)this.module).vSpeed.getValue() / 10.0F);
            double y = mc.player.input.jumping ? vertical : (mc.player.input.sneaking ? -vertical : 0.0D);
            if (MovementUtil.isMoving()) {
               event.setVec(new Vec3d(dir[0], y, dir[1]));
            } else {
               event.setVec(new Vec3d(0.0D, y, 0.0D));
            }

            mc.player.setVelocity(Vec3d.ZERO);
            mc.player.limbAnimator.setSpeed(0.0F);
         }
         break;
      case BOOST:
         if (((ElytraFly)this.module).isElytra() && mc.player.input.jumping) {
            float yaw = mc.player.getYaw() * 0.017453292F;
            double x = mc.player.getVelocity().getX() - (double)MathHelper.sin(yaw) * ((double)(Integer)((ElytraFly)this.module).factor.getValue() / 100.0D);
            double z = mc.player.getVelocity().getZ() + (double)MathHelper.cos(yaw) * ((double)(Integer)((ElytraFly)this.module).factor.getValue() / 100.0D);
            MovementUtil.setXZVelocity(x, z, mc.player);
         }
         break;
      case FIREWORK:
         int fireworkSlot = ItemUtil.getHotbarItemSlot(Items.FIREWORK_ROCKET);
         int lastSlot = mc.player.getInventory().selectedSlot;
         if (fireworkSlot == -1) {
            return;
         }

         if (((ElytraFly)this.module).isElytra() && ((ElytraFly)this.module).fireworkTimer.passed((double)((Float)((ElytraFly)this.module).delay.getValue() * 1000.0F))) {
            InventoryUtil.switchToSlot(fireworkSlot);
            ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
               return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
            });
            mc.player.swingHand(Hand.MAIN_HAND);
            InventoryUtil.switchToSlot(lastSlot);
            ((ElytraFly)this.module).fireworkTimer.reset();
         }
      }

   }
}