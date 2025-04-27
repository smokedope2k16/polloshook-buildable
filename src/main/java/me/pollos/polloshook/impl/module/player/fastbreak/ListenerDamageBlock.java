package me.pollos.polloshook.impl.module.player.fastbreak;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.impl.events.block.DamageBlockEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ListenerDamageBlock extends ModuleListener<FastBreak, DamageBlockEvent> {
   public ListenerDamageBlock(FastBreak module) {
      super(module, DamageBlockEvent.class);
   }

   public void call(DamageBlockEvent event) {
      BlockPos pos = event.getPos();
      if (PositionUtil.getEyesPos().squaredDistanceTo(pos.toCenterPos()) > (double)MathUtil.square((Float)((FastBreak)this.module).range.getValue())) {
         mc.player.swingHand(Hand.MAIN_HAND);
         event.setCanceled(true);
      } else if (((FastBreak)this.module).pos != null) {
         if (((FastBreak)this.module).pos.equals(pos)) {
            mc.player.swingHand(Hand.MAIN_HAND);
            event.setCanceled(true);
         }

      }
   }
}
