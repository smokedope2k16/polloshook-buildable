package me.pollos.polloshook.impl.module.render.breadcrumbs.util;

import net.minecraft.util.math.Vec3d;

public record TraceVectors(Vec3d vec, Vec3d vec2, long time) {
   public TraceVectors(Vec3d vec, Vec3d vec2, long time) {
      this.vec = vec;
      this.vec2 = vec2;
      this.time = time;
   }

   public Vec3d vec() {
      return this.vec;
   }

   public Vec3d vec2() {
      return this.vec2;
   }

   public long time() {
      return this.time;
   }
}
