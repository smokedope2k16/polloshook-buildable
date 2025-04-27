package me.pollos.polloshook.impl.module.movement.holesnap;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.rotations.RotationsUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ListenerMove extends ModuleListener<HoleSnap, MoveEvent> {
   public ListenerMove(HoleSnap module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      if (mc.player.horizontalCollision) {
         if (((HoleSnap)this.module).noMoveTicks++ >= 8 && ((HoleSnap)this.module).noMoveTicks != -55) {
            if ((Boolean)((HoleSnap)this.module).reRoute.getValue() && ((HoleSnap)this.module).hole != null) {
               ((HoleSnap)this.module).setTarget((Float)((HoleSnap)this.module).range.getValue() + (Float)((HoleSnap)this.module).extendRange.getValue(), ((HoleSnap)this.module).hole);
               ((HoleSnap)this.module).noMoveTicks = -55;
            } else {
               ((HoleSnap)this.module).toggle();
            }
         }
      } else if (((HoleSnap)this.module).noMoveTicks >= 1 || ((HoleSnap)this.module).noMoveTicks == -55) {
         ((HoleSnap)this.module).noMoveTicks = 0;
      }

      if ((Boolean)((HoleSnap)this.module).reRoute.getValue()) {
         boolean holeCheck = !this.isHole() || !((HoleSnap)this.module).isValidHole(((HoleSnap)this.module).lastHole, (Float)((HoleSnap)this.module).range.getValue());
         if (((HoleSnap)this.module).hole != null && holeCheck) {
            ((HoleSnap)this.module).setTarget((Float)((HoleSnap)this.module).range.getValue() + (Float)((HoleSnap)this.module).extendRange.getValue(), (Hole)null);
         }
      } else if (!this.isHole()) {
         ((HoleSnap)this.module).setEnabled(false);
         return;
      }

      if (((HoleSnap)this.module).hole != null && !EntityUtil.isSafe(mc.player) && !HoleUtil.isHole(mc.player.getBlockPos()) && !HoleUtil.isTerrainHole(mc.player.getBlockPos())) {
         Vec3d playerHolePos = ((HoleSnap)this.module).getPlayerHolePos(((HoleSnap)this.module).hole);
         if (playerHolePos == ((HoleSnap)this.module).IGNORE_POS) {
            ((HoleSnap)this.module).toggle();
         } else {
            double yawRad = Math.toRadians((double)RotationsUtil.getRotationsTo(mc.player.getPos(), playerHolePos).x);
            double speed = mc.player.isOnGround() && mc.player.squaredDistanceTo(playerHolePos) > (double)MathUtil.square(1.0F) ? -Math.min(0.2805D, mc.player.squaredDistanceTo(playerHolePos) / (double)MathUtil.square(2.0F)) : -MovementUtil.getDefaultMoveSpeed() + 0.02D;
            if (BlockUtil.getDistanceSq(playerHolePos) <= (double)MathUtil.square(0.1F)) {
               event.setXZ(0.0D, 0.0D);
            } else {
               float x = (float)(-Math.sin(yawRad) * speed);
               float z = (float)(Math.cos(yawRad) * speed);
               event.setXZ((double)x, (double)z);
            }
         }
      } else {
         ((HoleSnap)this.module).toggle();
      }
   }

   protected boolean isHole() {
      if (((HoleSnap)this.module).hole == null) {
         return false;
      } else {
         BlockPos first = ((HoleSnap)this.module).hole.getPos();
         if (!BlockUtil.isAir(first)) {
            return false;
         } else {
            return HoleUtil.isHole(first) || HoleUtil.isDoubleHole(first) || HoleUtil.isTerrainHole(first);
         }
      }
   }
}
