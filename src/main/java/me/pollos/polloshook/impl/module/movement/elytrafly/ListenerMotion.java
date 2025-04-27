package me.pollos.polloshook.impl.module.movement.elytrafly;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.movement.elytrafly.mode.ElytraFlyMode;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.math.MathHelper;

public class ListenerMotion extends ModuleListener<ElytraFly, MotionUpdateEvent> {
   public ListenerMotion(ElytraFly module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (((ElytraFly)this.module).isElytra()) {
         if ((Boolean)((ElytraFly)this.module).stopInWater.getValue() && mc.player.isTouchingWater()) {
            PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            return;
         }

         if ((Boolean)((ElytraFly)this.module).stopOnGround.getValue() && mc.player.isOnGround()) {
            PacketUtil.send(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
            return;
         }

         if (((ElytraFly)this.module).mode.getValue() == ElytraFlyMode.BOUNCE && (Boolean)((ElytraFly)this.module).pitchLock.getValue()) {
            event.setPitch((float)(Integer)((ElytraFly)this.module).pitch.getValue());
         }
      }

      if (((ElytraFly)this.module).mode.getValue() == ElytraFlyMode.CONTROL && (Boolean)((ElytraFly)this.module).pitchLock.getValue() && event.getStage() == Stage.PRE && ((ElytraFly)this.module).isElytra()) {
         float moveStrafe = mc.player.input.movementSideways;
         float moveForward = mc.player.input.movementForward;
         float strafe = moveStrafe * 90.0F * (moveForward != 0.0F ? 0.5F : 1.0F);
         Managers.getRotationManager().setRotations(MathHelper.wrapDegrees(mc.player.getYaw() - strafe - (float)(moveForward < 0.0F ? 180 : 0)), event.getPitch(), event);
      }

   }
}