package me.pollos.polloshook.impl.module.movement.longjump.type;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.longjump.LongJump;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedType;

public class LongJumpType extends SpeedType {
   protected float divFactor = 159.998F;
   protected float slope = 0.76F;
   protected final StopWatch airTimer = new StopWatch();

   public LongJumpType() {
      this.stage = 1;
      this.distance = 0.0D;
   }

   public void move(MoveEvent event) {
      if (!MovementUtil.isMoving()) {
         this.stage = 1;
      }

      if (this.stage == 2 && !mc.player.isOnGround()) {
         event.setY(event.getY() - 0.05000000074505806D);
      }

      if (this.stage == 1 && MovementUtil.isMoving()) {
         this.speed = (double)this.getBoost() * this.calcEffects(0.287F) - 0.01D;
      } else if (this.stage == 2 && mc.player.isOnGround()) {
         double height = this.calcJumpHeight((Boolean)this.longJumpInstance().getMini().getValue() ? 0.25F + this.getJumpHeight() / 100.0F : this.getJumpHeight());
         MovementUtil.setYVelocity(height, mc.player);
         event.setY(height);
         this.speed *= (double)this.getLongJumpSpeed();
         this.airTimer.reset();
      } else if (this.stage == 3 && this.airTimer.passed((Boolean)this.longJumpInstance().getMini().getValue() ? 40L : 400L)) {
         this.speed = this.distance - (double)this.slope * (this.distance - this.calcEffects() * (double)this.getBoost());
      } else {
         if (mc.world.getCollisions(mc.player, mc.player.getBoundingBox().offset(0.0D, mc.player.getVelocity().getY(), 0.0D)).spliterator().getExactSizeIfKnown() > 0L || mc.player.verticalCollision) {
            this.stage = 1;
         }

         this.speed = this.distance - this.distance / (double)this.divFactor;
      }

      ++this.stage;
      this.clamp();
      MovementUtil.strafe(event, this.speed);
      mc.player.setVelocity(event.getX(), mc.player.getVelocity().getY(), event.getZ());
   }

   public void onMotionUpdate(MotionUpdateEvent event) {
      super.onMotionUpdate(event);
   }

   public void reset() {
      Managers.getTimerManager().reset();
      this.stage = 0;
      this.distance = 0.0D;
   }

   protected void clamp() {
      this.speed = Math.max(this.speed, this.calcEffects() * (double)this.getBoost());
   }

   protected float getJumpHeight() {
      return 0.4012313F;
   }

   protected float getLongJumpSpeed() {
      return 2.149F;
   }

   protected LongJump longJumpInstance() {
      return (LongJump)Managers.getModuleManager().get(LongJump.class);
   }

   private float getBoost() {
      return (Float)this.longJumpInstance().getBoost().getValue();
   }
}
