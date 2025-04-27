package me.pollos.polloshook.impl.module.movement.speed.type.strafes;

import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.asm.ducks.entity.IEntity;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedType;

public class Strafe extends SpeedType {
   protected final double friction = 0.01D;
   protected double divFriction = 159.0D;
   protected double slope = 0.66D;
   protected boolean boost;
   protected final StopWatch timer = (new StopWatch()).reset();

   public Strafe() {
      this.stage = 4;
   }

   public void move(MoveEvent event) {
      double ySpeed = event.getY();
      if (this.stage == 1) {
         this.speed = 1.35D * this.calcEffects() - 0.01D;
      } else if (this.stage == 2 && mc.player.verticalCollision) {
         ySpeed = this.getJumpY();
         this.speed *= this.getSpeedBoost(this.boost);
         this.boost = !this.boost;
      } else if (this.stage == 3) {
         this.speed = this.distance - this.slope * (this.distance - this.calcEffects());
      } else {
         if (mc.player.verticalCollision || mc.world.getCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, mc.player.getVelocity().y, 0.0D)).iterator().hasNext()) {
            this.stage = 1;
         }

         this.speed = this.distance - this.distance / this.divFriction;
      }

      this.clamp();
      if (!((IEntity)mc.player).isInWeb()) {
         boolean liquid = PlayerUtil.isInLiquid();
         double y = !liquid && !((double)mc.player.fallDistance > 5.0D) ? ySpeed : mc.player.getVelocity().y;
         MovementUtil.strafe(event, y, liquid ? this.speed * 0.85D : this.speed, this.getMovementInput());
         mc.player.setVelocity(event.getX(), y, event.getZ());
      }

      ++this.stage;
   }

   public void reset() {
      this.stage = 4;
      this.distance = 0.0D;
      this.speed = 0.0D;
   }

   protected float getJumpHeight() {
      return 0.4F;
   }

   public void onKnockbackTaken(float multplier, float incomingSpeed) {
      if (this.timer.passed(500L)) {
         this.speed += (double)(incomingSpeed * multplier);
         this.distance += (double)(incomingSpeed * multplier);
         this.timer.reset();
      }

   }

   public double getSpeedBoost(boolean boost) {
      return boost ? 1.6835D : 1.395D;
   }

   private double getJumpY() {
      return mc.player.isInsideWall() ? this.calcJumpHeight(0.42F) : this.calcJumpHeight();
   }
}
