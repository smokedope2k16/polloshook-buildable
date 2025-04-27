package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ClickBlockEvent extends Event {
   private final BlockPos pos;
   private final Direction facing;

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public Direction getFacing() {
      return this.facing;
   }

   
   public ClickBlockEvent(BlockPos pos, Direction facing) {
      this.pos = pos;
      this.facing = facing;
   }
}
