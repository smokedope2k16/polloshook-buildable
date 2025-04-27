package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.asm.mixins.network.IExplosionS2CPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.velocity.mode.VelocityMode;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class ListenerExplode extends SafeModuleListener<Velocity, PacketEvent.Receive<ExplosionS2CPacket>> {
   public ListenerExplode(Velocity module) {
      super(module, PacketEvent.Receive.class, -1000000, ExplosionS2CPacket.class);
   }

   public void safeCall(PacketEvent.Receive<ExplosionS2CPacket> event) {
      ExplosionS2CPacket packet = (ExplosionS2CPacket)event.getPacket();
      IExplosionS2CPacket iPacket = (IExplosionS2CPacket)packet;
      switch((VelocityMode)((Velocity)this.module).mode.getValue()) {
      case NORMAL:
         if (((Velocity)this.module).notFull()) {
            iPacket.setX((float)((int)(packet.getPlayerVelocityX() * (Float)((Velocity)this.module).horizontal.getValue() / 100.0F)));
            iPacket.setY((float)((int)(packet.getPlayerVelocityY() * (Float)((Velocity)this.module).vertical.getValue() / 100.0F)));
            iPacket.setZ((float)((int)(packet.getPlayerVelocityZ() * (Float)((Velocity)this.module).horizontal.getValue() / 100.0F)));
         } else {
            this.handleCancel(event);
         }
         break;
      case GRIM:
         if ((Boolean)((Velocity)this.module).stopInWalls.getValue() && (BlockUtil.isSafe(mc.player.getBlockPos()) || BlockUtil.isSafe(mc.player.getBlockPos().up()))) {
            return;
         }

         if ((Boolean)((Velocity)this.module).lag.getValue() && !((Velocity)this.module).timer.passed(100L)) {
            return;
         }

         this.handleCancel(event);
         ((Velocity)this.module).cancel = true;
      }

   }

   private void handleCancel(PacketEvent.Receive<ExplosionS2CPacket> event) {
      ExplosionS2CPacket packet = (ExplosionS2CPacket)event.getPacket();
      IExplosionS2CPacket iPacket = (IExplosionS2CPacket)packet;
      iPacket.setX(0.0F);
      iPacket.setY(0.0F);
      iPacket.setZ(0.0F);
   }
}
