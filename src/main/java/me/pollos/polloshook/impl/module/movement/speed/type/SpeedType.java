package me.pollos.polloshook.impl.module.movement.speed.type;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.speed.Speed;
import me.pollos.polloshook.impl.module.movement.tickshift.TickShift;
import net.minecraft.client.input.Input;

public class SpeedType implements Minecraftable {
   public static final float DEFAULT_SPEED = 0.287F;
   public static final float DEFAULT_JUMP = 0.42F;
   protected int stage;
   protected double speed;
   protected double distance;

   public void move(MoveEvent event) {
   }

   public void reset() {
   }

   public void onMotionUpdate(MotionUpdateEvent event) {
      this.distance = MovementUtil.getDistance2D();
      TickShift TICK_SHIFT = (TickShift)Managers.getModuleManager().get(TickShift.class);
      if (TICK_SHIFT.isBoosting() && TICK_SHIFT.getBoosted() > 0) {
         this.distance /= (double)TICK_SHIFT.getBoosted();
      }

   }

   protected Input getMovementInput() {
      return ((Speed)Managers.getModuleManager().get(Speed.class)).getMovementInput();
   }

   protected void clamp() {
      this.speed = Math.max(this.speed, this.calcEffects());
   }

   protected float getJumpHeight() {
      return 0.42F;
   }

   public double calcJumpHeight() {
      return this.calcJumpHeight(this.getJumpHeight());
   }

   public double calcJumpHeight(float f) {
      return (double)f + MovementUtil.getJumpSpeed();
   }

   public double calcEffects() {
      return MovementUtil.calcEffects(0.28700000047683716D);
   }

   public double calcEffects(float i) {
      return MovementUtil.calcEffects((double)i);
   }
}
