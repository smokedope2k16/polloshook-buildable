package me.pollos.polloshook.api.minecraft.movement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.rotations.FacingUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PositionUtil implements Minecraftable {
   public static final double COLLISION_EPSILON = 1.0E-7D;

   public static Vec3d getEyesPos() {
      return getEyesPos(mc.player);
   }

   public static Vec3d getEyesPos(Entity entity) {
      return new Vec3d(entity.getX(), getEyeHeight(entity), entity.getZ());
   }

   public static double getEyeHeight(Entity entity) {
      return entity.getY() + (double)entity.getEyeHeight(entity.getPose());
   }

   public static Set<BlockPos> getBlockedPositions(Entity entity) {
      return getBlockedPositions(entity.getBoundingBox());
   }

   public static Set<BlockPos> getBlockedPositions(Box bb) {
      return getBlockedPositions(bb, 0.5D);
   }

   public static Set<BlockPos> getBlockedPositions(Box bb, double offset) {
      Set<BlockPos> positions = new HashSet();
      double y = bb.minY;
      if (bb.minY - Math.floor(bb.minY) > offset) {
         y = Math.ceil(bb.minY);
      }

      Box adjustedBB = bb.expand(-1.0E-7D);
      positions.add(BlockPos.ofFloored(adjustedBB.maxX, y, adjustedBB.maxZ));
      positions.add(BlockPos.ofFloored(adjustedBB.minX, y, adjustedBB.minZ));
      positions.add(BlockPos.ofFloored(adjustedBB.maxX, y, adjustedBB.minZ));
      positions.add(BlockPos.ofFloored(adjustedBB.minX, y, adjustedBB.maxZ));
      return positions;
   }

   public static Set<BlockPos> getSurroundOffsets(Entity entity) {
      return getSurroundOffsets(entity.getBoundingBox());
   }

   public static Set<BlockPos> getSurroundOffsets(Box box) {
      Set<BlockPos> blocked = getBlockedPositions(box);
      Set<BlockPos> surround = new HashSet();
      Iterator var3 = blocked.iterator();

      while(var3.hasNext()) {
         BlockPos pos = (BlockPos)var3.next();
         Direction[] var5 = FacingUtil.HORIZONTALS;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction facing = var5[var7];
            BlockPos offset = pos.offset(facing);
            if (!blocked.contains(offset)) {
               surround.add(offset);
            }
         }
      }

      return surround;
   }

   public static boolean inLiquid() {
      return inLiquid(MathHelper.floor(mc.player.getBoundingBox().minY + 0.01D));
   }

   public static boolean inLiquid(boolean feet) {
      return inLiquid(MathHelper.floor(mc.player.getBoundingBox().minY - (feet ? 0.03D : 0.2D)));
   }

   private static boolean inLiquid(int y) {
      return getFluidState(y) != null;
   }

   public static BlockState getFluidState(Box box) {
      return getFluidState(MathHelper.floor(box.minY - 0.2D));
   }

   public static BlockState getFluidState(int minY) {
      for(int i = MathHelper.floor(mc.player.getBoundingBox().minX); i < MathHelper.ceil(mc.player.getBoundingBox().maxX); ++i) {
         for(int j = MathHelper.floor(mc.player.getBoundingBox().minZ); j < MathHelper.ceil(mc.player.getBoundingBox().maxZ); ++j) {
            BlockState state = mc.world.getBlockState(new BlockPos(i, minY, j));
            if (state.getBlock() instanceof FluidBlock) {
               return state;
            }
         }
      }

      return null;
   }

   public static float getHeadHeight() {
      return mc.player.getHeight() * 0.85F;
   }

   public float getHeadHeight(Entity entity) {
      return entity.getHeight() * 0.85F;
   }

   public static boolean voxelShapeIntersect(Box box1, Box box2) {
      return box1.minX - box2.maxX < -1.0E-7D && box1.maxX - box2.minX > 1.0E-7D && box1.minY - box2.maxY < -1.0E-7D && box1.maxY - box2.minY > 1.0E-7D && box1.minZ - box2.maxZ < -1.0E-7D && box1.maxZ - box2.minZ > 1.0E-7D;
   }
}
