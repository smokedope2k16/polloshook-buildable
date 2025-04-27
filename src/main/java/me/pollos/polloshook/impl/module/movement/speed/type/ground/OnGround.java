package me.pollos.polloshook.impl.module.movement.speed.type.ground;

import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedType;

public class OnGround extends SpeedType {
   public OnGround() {
      this.stage = 2;
   }

   public void move(MoveEvent event) {
      if (mc.player.isOnGround() || this.stage == 3) {
         if (!mc.player.horizontalCollision && mc.player.forwardSpeed != 0.0F || mc.player.sidewaysSpeed != 0.0F) {
            if (this.stage == 2) {
               this.speed *= 2.149D;
               this.stage = 3;
            } else if (this.stage == 3) {
               this.stage = 2;
               this.speed = this.distance - 0.66D * (this.distance - this.calcEffects());
            } else if (this.isBoxColliding() || mc.player.verticalCollision) {
               this.stage = 1;
            }
         }

         this.clamp();
         MovementUtil.strafe(event, this.speed, this.getMovementInput());
      }

   }

   private boolean isBoxColliding() {
      return mc.world.getCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, 0.21D, 0.0D)).spliterator().getExactSizeIfKnown() > 0L;
   }

   public void reset() {
      this.distance = 0.0D;
      this.speed = 0.0D;
      this.stage = 2;
   }
}
