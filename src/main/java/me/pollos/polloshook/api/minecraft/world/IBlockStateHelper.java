package me.pollos.polloshook.api.minecraft.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface IBlockStateHelper extends WorldAccess {
   default void addAir(BlockPos pos) {
      this.addBlockState(pos, Blocks.AIR.getDefaultState());
   }

   void addBlockState(BlockPos var1, BlockState var2);

   void delete(BlockPos var1);

   void clearAllStates();
}