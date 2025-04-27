package me.pollos.polloshook.impl.module.movement.phase;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.phase.util.OffsetType;
import me.pollos.polloshook.impl.module.movement.phase.util.PhaseMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ListenerMove extends ModuleListener<Phase, MoveEvent> {
   public ListenerMove(Phase module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      if (((Phase)this.module).mode.getValue() == PhaseMode.CLIP) {
         double currentX = mc.player.getX() - (double)mc.player.getBlockX();
         double currentZ = mc.player.getZ() - (double)mc.player.getBlockZ();
         if (!((Phase)this.module).findCorner(currentX, currentZ, new BlockPos(mc.player.getBlockX(), (int)Math.round(mc.player.getY()), mc.player.getBlockZ()))) {
            double targetX = MathHelper.lerp(((Phase)this.module).cornerX, 0.3D, 0.7D);
            double targetZ = MathHelper.lerp(((Phase)this.module).cornerZ, 0.3D, 0.7D);
            double centerX = currentX - 0.5D;
            double centerZ = currentZ - 0.5D;
            if (Math.abs(centerX) >= 0.19999D && Math.signum(centerX) == Math.signum(targetX - 0.5D) && Math.abs(centerZ) >= 0.19999D && Math.signum(centerZ) == Math.signum(targetZ - 0.5D)) {
               this.wallMove(event, targetX, targetZ, currentX, currentZ);
            } else {
               this.outMove(event, targetX, targetZ, currentX, currentZ);
            }
         }
      }

   }

   private void wallMove(MoveEvent event, double targetX, double targetZ, double currentX, double currentZ) {
      double depth = ((OffsetType)((Phase)this.module).offset.getValue()).getDepth();
      targetX -= Math.signum(0.5D - targetX) * depth;
      targetZ -= Math.signum(0.5D - targetZ) * depth;
      double yaw = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(targetZ - currentZ, targetX - currentX)) - 90.0D);
      double x = Math.cos(Math.toRadians(yaw + 90.0D));
      double z = Math.sin(Math.toRadians(yaw + 90.0D));
      double dx = currentX - targetX;
      double dz = currentZ - targetZ;
      double finalMove = (Double)((Phase)this.module).movement.getValue() * 0.10000000149011612D;
      double dist = Math.min(Math.sqrt(dx * dx + dz * dz), finalMove);
      double mx = dist * x;
      double mz = dist * z;
      if (dist > 0.001D) {
         if (--((Phase)this.module).time < 0) {
            ((Phase)this.module).time = (Integer)((Phase)this.module).delay.getValue();
            double ox = (double)mc.player.getBlockX() + currentX + mx;
            double oz = (double)mc.player.getBlockZ() + currentZ + mz;
            mc.player.setPosition(ox, mc.player.getY(), oz);
            PacketUtil.send(new PositionAndOnGround(ox, mc.player.getY(), oz, true));
            event.setXZ(0.0D, 0.0D);
            ((Phase)this.module).doBounds = true;
         }
      } else {
         event.setXZ(0.0D, 0.0D);
      }

   }

   private void outMove(MoveEvent event, double targetX, double targetZ, double currentX, double currentZ) {
      double yaw = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(targetZ - currentZ, targetX - currentX)) - 90.0D);
      double x = Math.cos(Math.toRadians(yaw + 90.0D));
      double z = Math.sin(Math.toRadians(yaw + 90.0D));
      double dx = currentX - targetX;
      double dz = currentZ - targetZ;
      double dist = Math.min(Math.sqrt(dx * dx + dz * dz), MovementUtil.getDefaultMoveSpeed());
      event.setXZ(dist * x, dist * z);
   }
}
