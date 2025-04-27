package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.module.movement.noslow.mode.NoSlowClimbingMode;
import net.minecraft.util.math.Vec3d;

public class ListenerApplyClimbingSpeed extends ModuleListener<NoSlow, NoSlow.ApplyClimbingSpeedEvent> {
   public ListenerApplyClimbingSpeed(NoSlow module) {
      super(module, NoSlow.ApplyClimbingSpeedEvent.class);
   }

   public void call(NoSlow.ApplyClimbingSpeedEvent event) {
      if ((Boolean)((NoSlow)this.module).climbing.getValue() && ((NoSlow)this.module).mode.getValue() == NoSlowClimbingMode.SPEED) {
         Vec3d vec3 = event.getVec();
         event.setVec(new Vec3d(vec3.getX(), vec3.getY() * (double)(Float)((NoSlow)this.module).factor.getValue(), vec3.getZ()));
      }

   }
}
