package me.pollos.polloshook.api.minecraft.rotations;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.block.BlockUtil;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;

public final class RotationsUtil implements Minecraftable {

   private RotationsUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static float[] getMcPlayerRotations() {
      return new float[]{mc.player.getYaw(), mc.player.getPitch()};
   }

   public static float[] getRotationsFacing(BlockPos pos, Direction facing) {
      return getRotationsFacing(pos, facing, mc.player, mc.world, BlockUtil.getState(pos));
   }

   public static float[] getRotationsFacing(BlockPos pos, Direction facing, Entity from) {
      return getRotationsFacing(pos, facing, from, mc.world, BlockUtil.getState(pos));
   }

   public static float[] getRotationsFacing(BlockPos pos, Direction facing, Entity from, ClientWorld world, BlockState state) {
      VoxelShape shape = state.getOutlineShape(world, pos);
      double x = pos.getX() + (shape.getMin(Axis.X) + shape.getMax(Axis.X)) / 2.0D;
      double y = pos.getY() + (shape.getMin(Axis.Y) + shape.getMax(Axis.Y)) / 2.0D;
      double z = pos.getZ() + (shape.getMin(Axis.Z) + shape.getMax(Axis.Z)) / 2.0D;
      if (facing != null) {
         x += facing.getVector().getX() * ((shape.getMin(Axis.X) + shape.getMax(Axis.X)) / 2.0D);
         y += facing.getVector().getY() * ((shape.getMin(Axis.Y) + shape.getMax(Axis.Y)) / 2.0D);
         z += facing.getVector().getZ() * ((shape.getMin(Axis.Z) + shape.getMax(Axis.Z)) / 2.0D);
      }
      return getRotationsFacing(x, y, z, from);
   }

   public static float[] getRotationsFacing(double x, double y, double z, Entity entity) {
      double xDiff = x - entity.getX();
      double yDiff = y - (entity.getY() + entity.getEyeHeight(entity.getPose()));
      double zDiff = z - entity.getZ();
      double dist = MathHelper.sqrt((float)(xDiff * xDiff + zDiff * zDiff));
      float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0D / Math.PI));
      float prevYaw = mc.player.prevYaw;
      float diff = yaw - prevYaw;
      if (diff < -180.0F || diff > 180.0F) {
         float round = (float)Math.round(Math.abs(diff / 360.0F));
         diff = diff < 0.0F ? diff + 360.0F * round : diff - 360.0F * round;
      }
      return new float[]{prevYaw + diff, pitch};
   }

   public static float[] getRotations(double x, double y, double z) {
      double xDiff = x - mc.player.getX();
      double yDiff = y - PositionUtil.getEyeHeight(mc.player);
      double zDiff = z - mc.player.getZ();
      double dist = MathHelper.sqrt((float)(xDiff * xDiff + zDiff * zDiff));
      float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
      float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0D / Math.PI));
      float diff = yaw - mc.player.getYaw();
      if (diff < -180.0F || diff > 180.0F) {
         float round = (float)Math.round(Math.abs(diff / 360.0F));
         diff = diff < 0.0F ? diff + 360.0F * round : diff - 360.0F * round;
      }
      return new float[]{mc.player.getYaw() + diff, pitch};
   }

   public static Vec2f getRotationsTo(Vec3d posTo, Vec3d posFrom) {
      return getRotationFromVec(posTo.subtract(posFrom));
   }

   public static double normalizeAngle(double angleIn) {
      if ((angleIn %= 360.0D) >= 180.0D) {
         angleIn -= 360.0D;
      }
      if (angleIn < -180.0D) {
         angleIn += 360.0D;
      }
      return angleIn;
   }

   public static float normalizeAngle(float angleIn) {
      if ((angleIn %= 360.0F) >= 180.0F) {
         angleIn -= 360.0F;
      }
      if (angleIn < -180.0F) {
         angleIn += 360.0F;
      }
      return angleIn;
   }

   private static Vec2f getRotationFromVec(Vec3d vec) {
      double xz = Math.hypot(vec.x, vec.z);
      float yaw = (float)normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0D);
      float pitch = (float)normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, xz)));
      return new Vec2f(yaw, pitch);
   }

   public static boolean hastRotatedYaw(float[] rot1, float[] rot2, float error) {
      float yawDifference = Math.abs(normalizeAngle(rot1[0] - rot2[0]));
      float pitchDifference = rot1[1] - rot2[1];
      return yawDifference <= error && pitchDifference <= 30.0F;
   }

   public static double getYaw(Vec3d pos) {
      return mc.player.getYaw() + MathHelper.wrapDegrees((float)Math.toDegrees(Math.atan2(pos.getZ() - mc.player.getZ(), pos.getX() - mc.player.getX())) - 90.0F - mc.player.getYaw());
   }
}