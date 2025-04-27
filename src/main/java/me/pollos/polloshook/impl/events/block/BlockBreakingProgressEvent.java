package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.math.BlockPos;

public class BlockBreakingProgressEvent extends Event {
   private final BlockPos pos;

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public BlockBreakingProgressEvent(BlockPos pos) {
      this.pos = pos;
   }
}
