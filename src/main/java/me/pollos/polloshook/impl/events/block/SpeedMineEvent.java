package me.pollos.polloshook.impl.events.block;


import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.events.StageEvent;
import net.minecraft.util.math.BlockPos;

public class SpeedMineEvent extends StageEvent {
   private final BlockPos pos;

   public SpeedMineEvent(BlockPos pos, Stage stage) {
      super(stage);
      this.pos = pos;
   }

   
   public BlockPos getPos() {
      return this.pos;
   }
}
