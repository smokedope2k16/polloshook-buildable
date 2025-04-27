package me.pollos.polloshook.impl.module.movement.anchor;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.HoleUtil;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.obj.hole.Hole2x1;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ListenerMove extends ModuleListener<Anchor, MoveEvent> {
   public ListenerMove(Anchor module) {
      super(module, MoveEvent.class, -1000);
   }

   public void call(MoveEvent event) {
      if (!(mc.player.getPitch() >= (float)(Integer)((Anchor)this.module).pitch.getValue())) {
         ((Anchor)this.module).anchoring = false;
      } else {
         BlockPos playerPos = mc.player.getBlockPos();
         if (!EntityUtil.isInBurrow(mc.player) && !HoleUtil.isHole(playerPos) && !HoleUtil.isTerrainHole(playerPos) && !mc.player.isFallFlying()) {
            int height = 5;

            for(int i = 0; i <= height; ++i) {
               if (mc.world.isAir(playerPos.down(i + 1))) {
                  ((Anchor)this.module).anchoring = false;
               } else {
                  Hole hole = HoleUtil.getHole(playerPos.down(i), (Boolean)((Anchor)this.module).doubles.getValue(), true);
                  if (hole instanceof Hole2x1) {
                     Hole2x1 hole2x1 = (Hole2x1)hole;
                     if (hole2x1.isProtocolSafe()) {
                        break;
                     }
                  }

                  if (hole == null) {
                     ((Anchor)this.module).anchoring = false;
                     break;
                  }

                  Vec3d vec = HoleUtil.getCenter(hole);
                  if (hole instanceof Hole2x1) {
                     Hole2x1 hole2x1 = (Hole2x1)hole;
                     if (hole2x1.isProtocolSafe()) {
                        vec = HoleUtil.getCenter(hole2x1.getProtocolSafePart());
                     }
                  }

                  double veloX = mc.player.getVelocity().x;
                  double veloY = mc.player.getVelocity().y;
                  double veloZ = mc.player.getVelocity().z;
                  if ((Boolean)((Anchor)this.module).stopMotion.getValue()) {
                     mc.player.setVelocity(0.0D, veloY, 0.0D);
                     mc.player.input.movementForward = 0.0F;
                     mc.player.input.movementSideways = 0.0F;
                  }

                  if (veloY > -0.1D && (Boolean)((Anchor)this.module).fastFall.getValue()) {
                     mc.player.setVelocity(veloX, (double)(-((Float)((Anchor)this.module).speed.getValue() / 10.0F)), veloZ);
                  }

                  double xSpeed = vec.x - mc.player.getX();
                  double zSpeed = vec.z - mc.player.getZ();
                  event.setXZ(xSpeed / 2.0D, zSpeed / 2.0D);
                  ((Anchor)this.module).anchoring = true;
               }
            }

         } else {
            ((Anchor)this.module).anchoring = false;
         }
      }
   }
}
