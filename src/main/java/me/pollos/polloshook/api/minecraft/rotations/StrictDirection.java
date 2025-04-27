package me.pollos.polloshook.api.minecraft.rotations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.WorldAccess;

public final class StrictDirection implements Minecraftable {

   private StrictDirection() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static Direction getStrictDirection(BlockPos pos) {
      BlockPos eyePos = BlockPos.ofFloored(mc.player.getX(), PositionUtil.getEyeHeight(mc.player), mc.player.getZ());
      if (checkInside(pos, eyePos)) {
         return Direction.UP;
      } else {
         List<Direction> interactableDirections = getInteractableDirections(pos, eyePos, true);
         for (Direction direction : interactableDirections) {
            if (!isDirectionBlocked(pos, interactableDirections, direction, true)) {
               return direction;
            }
         }
         return null;
      }
   }

   public static boolean strictDirectionCheck(BlockPos pos, Direction direction) {
      return strictDirectionCheck(pos, direction, mc.world);
   }

   public static boolean strictDirectionCheck(BlockPos pos, Direction direction, WorldAccess world) {
      BlockPos eyePos = BlockPos.ofFloored(mc.player.getX(), PositionUtil.getEyeHeight(mc.player), mc.player.getZ());
      if (checkInside(pos, eyePos)) {
         return true;
      } else {
         BlockState state = world.getBlockState(pos);
         boolean fullBounds = state.getCollisionShape(mc.world, pos) == VoxelShapes.fullCube();
         List<Direction> interactableDirections = getInteractableDirections(pos, eyePos, fullBounds);
         if (!interactableDirections.contains(direction)) {
            return false;
         } else {
            return !isDirectionBlocked(pos, interactableDirections, direction, fullBounds);
         }
      }
   }

   private static boolean checkInside(BlockPos pos, BlockPos eyePos) {
      return eyePos.getX() == pos.getX() && eyePos.getY() == pos.getY() && eyePos.getZ() == pos.getZ();
   }

   private static List<Direction> getInteractableDirections(BlockPos pos, BlockPos eyePos, boolean fullBounds) {
      int locX = eyePos.getX();
      int locY = eyePos.getY();
      int locZ = eyePos.getZ();
      int blockX = pos.getX();
      int blockY = pos.getY();
      int blockZ = pos.getZ();
      return getDirections(locX - blockX, locY - blockY, locZ - blockZ, fullBounds);
   }

   private static List<Direction> getDirections(int xDiff, int yDiff, int zDiff, boolean fullBounds) {
      List<Direction> faces = new ArrayList<>(6);
      if (!fullBounds) {
         if (xDiff == 0) {
            faces.add(Direction.EAST);
            faces.add(Direction.WEST);
         }
         if (zDiff == 0) {
            faces.add(Direction.SOUTH);
            faces.add(Direction.NORTH);
         }
      }
      if (yDiff == 0) {
         faces.add(Direction.UP);
         faces.add(Direction.DOWN);
      } else {
         faces.add(yDiff > 0 ? Direction.UP : Direction.DOWN);
      }
      if (xDiff != 0) {
         faces.add(xDiff > 0 ? Direction.EAST : Direction.WEST);
      }
      if (zDiff != 0) {
         faces.add(zDiff > 0 ? Direction.SOUTH : Direction.NORTH);
      }
      return faces;
   }

   private static boolean isDirectionBlocked(BlockPos pos, List<Direction> interactable, Direction direction, boolean hasFullBounds) {
      if (hasFullBounds) {
         BlockPos relative = pos.offset(direction);
         BlockState state = mc.world.getBlockState(relative);
         return state.getCollisionShape(mc.world, pos) == VoxelShapes.fullCube() && !state.getCollisionShape(mc.world, relative).isEmpty();
      } else {
         for (Direction dir : interactable) {
            BlockPos relative = pos.offset(dir);
            BlockState state = mc.world.getBlockState(relative);
            boolean fullBounds = state.getCollisionShape(mc.world, pos) == VoxelShapes.fullCube();
            if (fullBounds && state.getCollisionShape(mc.world, relative).isEmpty()) {
               return false;
            }
         }
         return true;
      }
   }
}