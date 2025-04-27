package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.block.Block;

public class VelocityMultiplierEvent extends Event {
   private Block block;

   
   public Block getBlock() {
      return this.block;
   }

   
   public void setBlock(Block block) {
      this.block = block;
   }

   
   public VelocityMultiplierEvent(Block block) {
      this.block = block;
   }
}
