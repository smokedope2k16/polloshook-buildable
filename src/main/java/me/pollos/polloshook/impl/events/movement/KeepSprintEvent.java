package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.math.Vec3d;

public class KeepSprintEvent extends Event {
   private Vec3d motion;

   
   public Vec3d getMotion() {
      return this.motion;
   }

   
   public void setMotion(Vec3d motion) {
      this.motion = motion;
   }

   
   public KeepSprintEvent(Vec3d motion) {
      this.motion = motion;
   }
}
