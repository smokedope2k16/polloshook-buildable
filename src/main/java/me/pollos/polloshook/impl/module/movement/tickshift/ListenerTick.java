package me.pollos.polloshook.impl.module.movement.tickshift;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.movement.tickshift.mode.TickShiftMode;

public class ListenerTick extends ModuleListener<TickShift, TickEvent.Post> {
   public ListenerTick(TickShift module) {
      super(module, TickEvent.Post.class);
   }

   public void call(TickEvent.Post event) {
      if (mc.player != null) {
         ((TickShift)this.module).boosting = false;
         if (((TickShift)this.module).mode.getValue() != TickShiftMode.INSTANT || ((TickShift)this.module).boosted != 0 && !EntityUtil.isSafe(mc.player)) {
            boolean instantCheck = ((TickShift)this.module).mode.getValue() == TickShiftMode.INSTANT && (MovementUtil.hasVelocity(mc.player) || !(Boolean)((TickShift)this.module).velocityCheck.getValue());
            if (Interpolation.getRenderEntity() == mc.player && (MovementUtil.anyMovementKeysWASD() || instantCheck)) {
               ((TickShift)this.module).timer.reset();
               if (((TickShift)this.module).boosted > 0) {
                  ((TickShift)this.module).boosting = true;
                  int boost = (Integer)((TickShift)this.module).perTick.getValue();
                  if (((TickShift)this.module).boosted < (Integer)((TickShift)this.module).perTick.getValue()) {
                     boost = ((TickShift)this.module).boosted;
                  }

                  PlayerUtil.tick(boost);
                  int decrease = ((TickShift)this.module).boosted - boost;
                  if (decrease < 0) {
                     decrease = 0;
                  }

                  ((TickShift)this.module).boosted = decrease;
               }
            }

            if (((TickShift)this.module).timer.passed(500L) && ((TickShift)this.module).chargeTimer.passed(50L / (long)(Integer)((TickShift)this.module).chargeSpeed.getValue()) && ((TickShift)this.module).boosted < (Integer)((TickShift)this.module).maxTicks.getValue() && !MovementUtil.hasVelocity(mc.player) && !MovementUtil.anyMovementKeysWASD()) {
               ++((TickShift)this.module).boosted;
               ((TickShift)this.module).chargeTimer.reset();
            }

         } else {
            ((TickShift)this.module).setEnabled(false);
         }
      }
   }
}
