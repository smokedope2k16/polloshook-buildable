package me.pollos.polloshook.impl.module.movement.speed.type.strafes;

import me.pollos.polloshook.impl.events.movement.MoveEvent;

public class StrafeStrict extends Strafe {
   public void move(MoveEvent event) {
      super.move(event);
   }

   public void reset() {
      super.reset();
   }

   public double getSpeedBoost(boolean boost) {
      return 1.408D;
   }

   protected float getJumpHeight() {
      return super.getJumpHeight();
   }
}
