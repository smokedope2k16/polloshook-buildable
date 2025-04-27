package me.pollos.polloshook.impl.module.movement.speed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedTypeEnum;
import me.pollos.polloshook.impl.module.movement.speed.type.strafes.Strafe;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class ListenerEntityVelocity extends ModuleListener<Speed, PacketEvent.Receive<EntityVelocityUpdateS2CPacket>> {
   public ListenerEntityVelocity(Speed module) {
      super(module, PacketEvent.Receive.class, EntityVelocityUpdateS2CPacket.class);
   }

   public void call(PacketEvent.Receive<EntityVelocityUpdateS2CPacket> event) {
      EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket)event.getPacket();
      if (mc.player != null && packet.getEntityId() == mc.player.getId()) {
         float speed = (float)(Math.sqrt(packet.getVelocityX() * packet.getVelocityX() + packet.getVelocityZ() * packet.getVelocityZ()) / 8000.0D);
         if (((SpeedTypeEnum)((Speed)this.module).mode.getValue()).isStrafe() && (Boolean)((Speed)this.module).strafeBoost.getValue()) {
            Strafe strafe = (Strafe)((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType();
            strafe.onKnockbackTaken((Float)((Speed)this.module).boostFactor.getValue(), speed);
         }
      }

   }
}
