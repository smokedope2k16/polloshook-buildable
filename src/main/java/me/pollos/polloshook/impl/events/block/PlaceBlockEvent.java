package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockEvent extends Event {
   private final Block block;
   private final BlockPos pos;

   
   public Block getBlock() {
      return this.block;
   }

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public PlaceBlockEvent(Block block, BlockPos pos) {
      this.block = block;
      this.pos = pos;
   }
}
