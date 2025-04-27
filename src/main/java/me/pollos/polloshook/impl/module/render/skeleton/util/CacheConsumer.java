package me.pollos.polloshook.impl.module.render.skeleton.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;

public class CacheConsumer implements VertexConsumer {
   private Vec3d[] currentArray;
   public final List<Vec3d[]> positions = new ArrayList();
   private int i = 0;

   public void start() {
      this.positions.clear();
      this.startArray();
   }

   private void startArray() {
      this.currentArray = new Vec3d[4];
      this.i = 0;
   }

   public VertexConsumer vertex(float x, float y, float z) {
      this.currentArray[this.i++] = new Vec3d((double)x, (double)y, (double)z);
      if (this.i >= 4) {
         this.positions.add(this.currentArray);
         this.startArray();
      }

      return this;
   }

   public VertexConsumer color(int red, int green, int blue, int alpha) {
      return this;
   }

   public VertexConsumer texture(float u, float v) {
      return this;
   }

   public VertexConsumer overlay(int u, int v) {
      return this;
   }

   public VertexConsumer light(int u, int v) {
      return this;
   }

   public VertexConsumer normal(float x, float y, float z) {
      return this;
   }
}
