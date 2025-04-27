package me.pollos.polloshook.impl.module.movement.speed.type;


import me.pollos.polloshook.impl.module.movement.speed.type.ground.OnGround;
import me.pollos.polloshook.impl.module.movement.speed.type.strafes.Strafe;
import me.pollos.polloshook.impl.module.movement.speed.type.strafes.StrafeStrict;

public enum SpeedTypeEnum {
   STRAFE(new Strafe()),
   STRAFE_STRICT(new StrafeStrict()),
   ON_GROUND(new OnGround());

   private final SpeedType type;

   public boolean isStrafe() {
      return this.type instanceof Strafe;
   }

   
   public SpeedType getType() {
      return this.type;
   }

   
   private SpeedTypeEnum(final SpeedType type) {
      this.type = type;
   }

   // $FF: synthetic method
   private static SpeedTypeEnum[] $values() {
      return new SpeedTypeEnum[]{STRAFE, STRAFE_STRICT, ON_GROUND};
   }
}
