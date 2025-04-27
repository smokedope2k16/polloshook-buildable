package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.asm.mixins.network.IEntityVelocityUpdateS2CPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.velocity.mode.VelocityMode;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class ListenerVelocity extends SafeModuleListener<Velocity, PacketEvent.Receive<EntityVelocityUpdateS2CPacket>> {
   public ListenerVelocity(Velocity module) {
      super(module, PacketEvent.Receive.class, EntityVelocityUpdateS2CPacket.class);
   }

   public void safeCall(PacketEvent.Receive<EntityVelocityUpdateS2CPacket> event) {
      EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket)event.getPacket();
      IEntityVelocityUpdateS2CPacket iPacket = (IEntityVelocityUpdateS2CPacket)packet;
      if (((EntityVelocityUpdateS2CPacket)event.getPacket()).getEntityId() == mc.player.getId()) {
         switch((VelocityMode)((Velocity)this.module).mode.getValue()) {
         case NORMAL:
            if (!((Velocity)this.module).notFull()) {
               event.setCanceled(true);
            } else {
               iPacket.setX((int)(packet.getVelocityX() * (double)(Float)((Velocity)this.module).horizontal.getValue() / 100.0D));
               iPacket.setY((int)(packet.getVelocityY() * (double)(Float)((Velocity)this.module).vertical.getValue() / 100.0D));
               iPacket.setZ((int)(packet.getVelocityZ() * (double)(Float)((Velocity)this.module).horizontal.getValue() / 100.0D));
            }
            break;
         case GRIM:
            if ((Boolean)((Velocity)this.module).stopInWalls.getValue() && (BlockUtil.isSafe(mc.player.getBlockPos()) || BlockUtil.isSafe(mc.player.getBlockPos().up()))) {
               return;
            }

            if ((Boolean)((Velocity)this.module).lag.getValue() && !((Velocity)this.module).timer.passed(100L)) {
               return;
            }

            event.setCanceled(true);
            ((Velocity)this.module).cancel = true;
         }
      }

   }
}
