package me.pollos.polloshook.api.minecraft.render;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class Interpolation implements Minecraftable {
   public static Entity getRenderEntity() {
      return (Entity)(mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity());
   }

   public static Vec3d interpolateEntity(Entity entity) {
      double x = interpolateLastTickPos(entity.getX(), entity.prevX) - getCameraPos().x;
      double y = interpolateLastTickPos(entity.getY(), entity.prevY) - getCameraPos().y;
      double z = interpolateLastTickPos(entity.getZ(), entity.prevZ) - getCameraPos().z;
      return new Vec3d(x, y, z);
   }

   public static double interpolateLastTickPos(double pos, double lastPos) {
      return lastPos + (pos - lastPos) * (double)mc.getRenderTickCounter().getTickDelta(false);
   }

   public static Box interpolatePos(BlockPos pos) {
      return interpolatePos(pos, 1.0F);
   }

   public static Box mergeBoxes(Box first, Box second) {
      return new Box(Math.min(first.minX, second.minX), Math.min(first.minY, second.minY), Math.min(first.minZ, second.minZ), Math.max(first.maxX, second.maxX), Math.max(first.maxY, second.maxY), Math.max(first.maxZ, second.maxZ));
   }

   public static Box interpolatePos(BlockPos pos, float height) {
      return new Box((double)pos.getX() - getCameraPos().x, (double)pos.getY() - getCameraPos().y, (double)pos.getZ() - getCameraPos().z, (double)pos.getX() - getCameraPos().x + 1.0D, (double)pos.getY() - getCameraPos().y + (double)height, (double)pos.getZ() - getCameraPos().z + 1.0D);
   }

   public static Box interpolateVec(Vec3d pos, float height) {
      return new Box(pos.getX() - getCameraPos().x, pos.getY() - getCameraPos().y, pos.getZ() - getCameraPos().z, pos.getX() - getCameraPos().x + 1.0D, pos.getY() - getCameraPos().y + (double)height, pos.getZ() - getCameraPos().z + 1.0D);
   }

   public static Box interpolateAxis(Box bb) {
      return new Box(bb.minX - getCameraPos().x, bb.minY - getCameraPos().y, bb.minZ - getCameraPos().z, bb.maxX - getCameraPos().x, bb.maxY - getCameraPos().y, bb.maxZ - getCameraPos().z);
   }

   public static Vec3d interpolateVec(Vec3d vec) {
      return new Vec3d(vec.getX() - getCameraPos().x, vec.getY() - getCameraPos().y, vec.getZ() - getCameraPos().z);
   }

   public static boolean isVisible(Box bb, RenderEvent event) {
      Frustum frustum = new Frustum(event.getPositionMatrix(), event.getProjectionMatrix());
      return frustum.isVisible(bb);
   }

   public static Vec3d getCameraPos() {
      Camera camera = mc.getEntityRenderDispatcher().camera;
      return camera == null ? Vec3d.ZERO : camera.getPos();
   }

   public static Vec3d getMcPlayerInterpolation() {
      Entity renderEntity = getRenderEntity();
      return interpolateEntity(renderEntity);
   }

   public static Box getInterpolatedBox(Entity entity, Vec3d interpolation) {
      return (new Box(0.0D, 0.0D, 0.0D, (double)entity.getWidth(), (double)entity.getHeight(), (double)entity.getWidth())).offset(interpolation.x - (double)(entity.getWidth() / 2.0F), interpolation.y, interpolation.z - (double)(entity.getWidth() / 2.0F)).expand(0.05D);
   }

   public static double getRenderPosX() {
      return getCameraPos().x;
   }

   public static double getRenderPosY() {
      return getCameraPos().y;
   }

   public static double getRenderPosZ() {
      return getCameraPos().z;
   }
}
