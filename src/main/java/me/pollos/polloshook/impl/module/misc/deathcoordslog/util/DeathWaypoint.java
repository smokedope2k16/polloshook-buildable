package me.pollos.polloshook.impl.module.misc.deathcoordslog.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public record DeathWaypoint(Vec3d vec, long time, DimensionType dimension) {
   public DeathWaypoint(Vec3d vec, long time, DimensionType dimension) {
      this.vec = vec;
      this.time = time;
      this.dimension = dimension;
   }

   public Vec3d vec() {
      return this.vec;
   }

   public long time() {
      return this.time;
   }

   public DimensionType dimension() {
      return this.dimension;
   }
}
