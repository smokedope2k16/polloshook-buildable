package me.pollos.polloshook.impl.module.render.breadcrumbs.util;

import net.minecraft.util.math.Vec3d;

public record TimedVec3d(Vec3d vec, Long time) {
   public TimedVec3d(Vec3d vec, Long time) {
      this.vec = vec;
      this.time = time;
   }

   public Vec3d vec() {
      return this.vec;
   }

   public Long time() {
      return this.time;
   }
}
