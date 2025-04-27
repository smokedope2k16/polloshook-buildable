package me.pollos.polloshook.api.minecraft.rotations;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class RaycastUtil implements Minecraftable {
   public static BlockHitResult raycast(WorldAccess world, RaycastContext context, boolean thorughBlocks) {
      Vec3d diff = context.getEnd().subtract(context.getStart());
      return diff.lengthSquared() < 1.0E-7D ? BlockHitResult.createMissed(context.getEnd(), Direction.getFacing(diff.x, diff.y, diff.z), BlockPos.ofFloored(context.getEnd())) : (BlockHitResult)BlockView.raycast(context.getStart(), context.getEnd(), context, (clipContext, pos) -> {
         BlockState state = world.getBlockState(pos);
         if (thorughBlocks && world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN && world.getBlockState(pos.down()).getBlock() != Blocks.ANVIL && state.getBlock().getBlastResistance() < 600.0F) {
            state = Blocks.BEDROCK.getDefaultState();
         }

         if (state.isAir()) {
            return null;
         } else {
            VoxelShape shape = clipContext.getBlockShape(state, world, pos);
            return Box.raycast(shape.getBoundingBoxes(), clipContext.getStart(), clipContext.getEnd(), pos);
         }
      }, (c) -> {
         return BlockHitResult.createMissed(context.getEnd(), Direction.getFacing(diff.x, diff.y, diff.z), BlockPos.ofFloored(context.getEnd()));
      });
   }

   public static Vec3d getRotationVector(float pitch, float yaw) {
      float f = pitch * 0.017453292F;
      float g = -yaw * 0.017453292F;
      float h = MathHelper.cos(g);
      float i = MathHelper.sin(g);
      float j = MathHelper.cos(f);
      float k = MathHelper.sin(f);
      return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
   }

   public static boolean hasLineOfSight(Vec3d wallVec) {
      return mc.world.raycast(new RaycastContext(PositionUtil.getEyesPos(), wallVec, ShapeType.COLLIDER, FluidHandling.NONE, mc.player)).getType() != Type.MISS;
   }

   public static boolean raytrace(Vec3d from, Vec3d to, BlockPos pos) {
      BlockHitResult result = mc.world.raycast(new RaycastContext(from, to, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
      return result != null && result.getType() == Type.MISS && result.getBlockPos().equals(pos);
   }

   public static boolean entityRaytrace(Vec3d from, Vec3d to) {
      return mc.world.raycast(new RaycastContext(from, to, ShapeType.COLLIDER, FluidHandling.NONE, mc.player)).getType() != Type.MISS;
   }
}