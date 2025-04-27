package me.pollos.polloshook.impl.module.movement.longjump.type;

import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;

public class AlternativeLongJump extends LongJumpType {
   public AlternativeLongJump() {
      this.slope = 0.66F;
   }

   public void move(MoveEvent event) {
      if (MathUtil.round(mc.player.getY() - (double)((int)mc.player.getY()), 3) == MathUtil.round(0.943D, 3)) {
         mc.player.setVelocity(mc.player.getVelocity().subtract(0.0D, 0.03D, 0.0D));
         event.setY(event.getY() - 0.03D);
      }

      super.move(event);
   }

   public void reset() {
      super.reset();
   }

   protected float getJumpHeight() {
      return 0.424F;
   }

   protected float getLongJumpSpeed() {
      return 2.149802F;
   }
}
