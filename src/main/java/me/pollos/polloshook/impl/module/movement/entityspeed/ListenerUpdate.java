package me.pollos.polloshook.impl.module.movement.entityspeed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.BlockPos;

public class ListenerUpdate extends ModuleListener<EntitySpeed, UpdateEvent> {
   protected final StopWatch jumpTimer = new StopWatch();
   protected final StopWatch stuckTimer = new StopWatch();

   public ListenerUpdate(EntitySpeed module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.player.getVehicle() != null) {
         if (this.isStuck()) {
            this.strafe(0.0D);
            this.stuckTimer.reset();
         } else if (mc.player.input.jumping) {
            this.jumpTimer.reset();
         } else if (this.jumpTimer.passed(1000L) && !mc.player.getVehicle().isInFluid() && !PositionUtil.inLiquid() && (this.stuckTimer.passed(1000L) || !(Boolean)((EntitySpeed)this.module).antiStuck.getValue())) {
            if (mc.player.getVehicle().isOnGround()) {
               MovementUtil.setYVelocity(0.4D, mc.player.getVehicle());
            }

            MovementUtil.setYVelocity(-0.4D, mc.player.getVehicle());
            this.strafe((double)(Float)((EntitySpeed)this.module).speed.getValue());
            if ((Boolean)((EntitySpeed)this.module).antiStuck.getValue()) {
               this.stuckTimer.reset();
            }

         }
      }
   }

   private boolean isStuck() {
      if (!(Boolean)((EntitySpeed)this.module).antiStuck.getValue()) {
         return false;
      } else {
         double cosYaw = Math.cos(Math.toRadians((double)(mc.player.getYaw() + 90.0F)));
         double sinYaw = Math.sin(Math.toRadians((double)(mc.player.getYaw() + 90.0F)));
         BlockPos pos = BlockPos.ofFloored(mc.player.getX() + 2.0D * cosYaw + 0.0D * sinYaw, mc.player.getY(), mc.player.getZ() + (2.0D * sinYaw - 0.0D * cosYaw));
         BlockPos down = BlockPos.ofFloored(mc.player.getX() + 2.0D * cosYaw + 0.0D * sinYaw, mc.player.getY() - 1.0D, mc.player.getZ() + (2.0D * sinYaw - 0.0D * cosYaw));
         if (!mc.player.getVehicle().isOnGround() && !mc.world.getBlockState(pos).blocksMovement() && !mc.world.getBlockState(down).blocksMovement()) {
            return true;
         } else {
            pos = BlockPos.ofFloored(mc.player.getX() + 2.0D * cosYaw + 0.0D * sinYaw, mc.player.getY(), mc.player.getZ() + (2.0D * sinYaw - 0.0D * cosYaw));
            return mc.world.getBlockState(pos).blocksMovement();
         }
      }
   }

   private void strafe(double speed) {
      Input input = mc.player.input;
      double forward = (double)input.movementForward;
      double strafe = (double)input.movementSideways;
      float yaw = mc.player.getYaw();
      if (forward == 0.0D && strafe == 0.0D) {
         MovementUtil.setXZVelocity(0.0D, 0.0D, mc.player.getVehicle());
      } else {
         if (forward != 0.0D) {
            if (strafe > 0.0D) {
               yaw += forward > 0.0D ? -45.0F : 45.0F;
            } else if (strafe < 0.0D) {
               yaw += forward > 0.0D ? 45.0F : -45.0F;
            }

            strafe = 0.0D;
            if (forward > 0.0D) {
               forward = 1.0D;
            } else if (forward < 0.0D) {
               forward = -1.0D;
            }
         }

         double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
         double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
         double[] dub = new double[]{forward * speed * cos + strafe * speed * sin, forward * speed * sin - strafe * speed * cos};
         MovementUtil.setXZVelocity(dub, mc.player.getVehicle());
      }
   }
}
