package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class InteractEvent extends Event {
   private final Hand hand;
   private final BlockPos pos;
   private final Direction direction;

   
   public Hand getHand() {
      return this.hand;
   }

   
   public BlockPos getPos() {
      return this.pos;
   }

   
   public Direction getDirection() {
      return this.direction;
   }

   
   public InteractEvent(Hand hand, BlockPos pos, Direction direction) {
      this.hand = hand;
      this.pos = pos;
      this.direction = direction;
   }
}
