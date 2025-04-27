package me.pollos.polloshook.impl.module.movement.speed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedTypeEnum;
import me.pollos.polloshook.impl.module.movement.speed.type.strafes.Strafe;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

public class ListenerExplode extends ModuleListener<Speed, PacketEvent.Receive<ExplosionS2CPacket>> {
   public ListenerExplode(Speed module) {
      super(module, PacketEvent.Receive.class, ExplosionS2CPacket.class);
   }

   public void call(PacketEvent.Receive<ExplosionS2CPacket> event) {
      ExplosionS2CPacket packet = (ExplosionS2CPacket)event.getPacket();
      Vec3d pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
      if (mc.player != null && (Boolean)((Speed)this.module).strafeBoost.getValue() && mc.player.squaredDistanceTo(pos) <= (double)MathUtil.square(50.0F)) {
         float speed = (float)Math.sqrt((double)(packet.getPlayerVelocityX() * packet.getPlayerVelocityX() + packet.getPlayerVelocityZ() * packet.getPlayerVelocityZ()));
         if (((SpeedTypeEnum)((Speed)this.module).mode.getValue()).isStrafe() && (Boolean)((Speed)this.module).strafeBoost.getValue()) {
            Strafe strafe = (Strafe)((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType();
            strafe.onKnockbackTaken((Float)((Speed)this.module).boostFactor.getValue(), speed);
         }
      }

   }
}
