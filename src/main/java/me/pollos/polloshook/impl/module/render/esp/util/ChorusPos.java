package me.pollos.polloshook.impl.module.render.esp.util;


import me.pollos.polloshook.api.interfaces.Labeled;
import net.minecraft.util.math.Vec3d;

public class ChorusPos implements Labeled {
   private final String label;
   private final Vec3d vec;
   private final long time;

   public String getLabel() {
      return this.label;
   }

   
   public Vec3d getVec() {
      return this.vec;
   }

   
   public long getTime() {
      return this.time;
   }

   
   public ChorusPos(String label, Vec3d vec, long time) {
      this.label = label;
      this.vec = vec;
      this.time = time;
   }
}
