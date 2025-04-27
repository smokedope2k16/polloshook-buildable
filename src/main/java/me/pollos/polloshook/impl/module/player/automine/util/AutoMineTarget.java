package me.pollos.polloshook.impl.module.player.automine.util;


import net.minecraft.util.math.BlockPos;

public class AutoMineTarget {
   private final AutoMinePriority priority;
   private final BlockPos pos;

   
   public AutoMinePriority getPriority() {
      return this.priority;
   }

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public AutoMineTarget(AutoMinePriority priority, BlockPos pos) {
      this.priority = priority;
      this.pos = pos;
   }
}
