package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.movement.velocity.mode.VelocityMode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.util.math.Direction;

public class ListenerTick extends SafeModuleListener<Velocity, TickEvent> {
   public ListenerTick(Velocity module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (!(Boolean)((Velocity)this.module).lag.getValue() || ((Velocity)this.module).timer.passed(100L)) {
         if (((Velocity)this.module).mode.getValue() == VelocityMode.GRIM && ((Velocity)this.module).cancel) {
            PacketUtil.send(this.getPacket());
            PacketUtil.send(new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, mc.player.isCrawling() ? mc.player.getBlockPos() : mc.player.getBlockPos().up(), Direction.DOWN));
            ((Velocity)this.module).cancel = false;
         }

      }
   }

   private Full getPacket() {
      double x = (Boolean)((Velocity)this.module).serverPos.getValue() ? Managers.getPositionManager().getX() : mc.player.getX();
      double y = (Boolean)((Velocity)this.module).serverPos.getValue() ? Managers.getPositionManager().getY() : mc.player.getY();
      double z = (Boolean)((Velocity)this.module).serverPos.getValue() ? Managers.getPositionManager().getZ() : mc.player.getZ();
      float yaw = (Boolean)((Velocity)this.module).clientRots.getValue() ? Managers.getRotationManager().getYaw() : mc.player.getYaw();
      float pitch = (Boolean)((Velocity)this.module).clientRots.getValue() ? Managers.getRotationManager().getPitch() : mc.player.getPitch();
      return new Full(x, y, z, yaw, pitch, mc.player.isOnGround());
   }
}