package me.pollos.polloshook.api.minecraft.rotations;

import java.util.Map;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FacingUtil implements Minecraftable {
   public static Direction[] TOP;
   public static Direction[] DOWN;
   public static Direction[] HORIZONTALS;

   public static Direction getFacing(BlockPos pos) {
      return getFacing(pos, mc.world);
   }

   public static Direction getFacing(BlockPos pos, ClientWorld provider) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction facing = var2[var4];
         if (!provider.getBlockState(pos.offset(facing)).isReplaceable()) {
            return facing;
         }
      }

      return null;
   }

   public static Direction getFacing(BlockPos pos, Map<BlockPos, BlockState> states) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction facing = var2[var4];
         BlockPos offset = pos.offset(facing);
         BlockState state = (BlockState)states.get(offset);
         if (state != null && !state.isReplaceable()) {
            return facing;
         }
      }

      return null;
   }

   static {
      TOP = new Direction[]{Direction.UP, Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST};
      DOWN = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH};
      HORIZONTALS = new Direction[]{Direction.NORTH, Direction.WEST, Direction.EAST, Direction.SOUTH};
   }
}