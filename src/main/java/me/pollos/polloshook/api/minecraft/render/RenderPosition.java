package me.pollos.polloshook.api.minecraft.render;

import net.minecraft.util.math.BlockPos;

public class RenderPosition {
   private final BlockPos pos;
   private long time = System.currentTimeMillis();

   public RenderPosition(BlockPos pos) {
      this.pos = pos;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }
}