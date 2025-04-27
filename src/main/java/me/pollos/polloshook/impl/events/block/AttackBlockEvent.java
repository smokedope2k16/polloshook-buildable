package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AttackBlockEvent extends Event {
   private final BlockPos pos;
   private final Direction direction;

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public Direction getDirection() {
      return this.direction;
   }

   
   public AttackBlockEvent(BlockPos pos, Direction direction) {
      this.pos = pos;
      this.direction = direction;
   }
}
