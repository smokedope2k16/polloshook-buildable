package me.pollos.polloshook.impl.module.movement.holesnap;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<HoleSnap, TickEvent.Post> {
   public ListenerTick(HoleSnap module) {
      super(module, TickEvent.Post.class);
   }

   public void call(TickEvent.Post event) {
      if ((Boolean)((HoleSnap)this.module).speed.getValue()) {
         if (Interpolation.getRenderEntity() == mc.player && (MovementUtil.anyMovementKeysWASD() || MovementUtil.hasVelocity(mc.player)) && ((HoleSnap)this.module).boosted > 0) {
            int boost = (Integer)((HoleSnap)this.module).factor.getValue();
            if (((HoleSnap)this.module).boosted < (Integer)((HoleSnap)this.module).factor.getValue()) {
               boost = ((HoleSnap)this.module).boosted;
            }

            PlayerUtil.tick(boost);
            int decrease = ((HoleSnap)this.module).boosted - boost;
            if (decrease < 0) {
               decrease = 0;
            }

            ((HoleSnap)this.module).boosted = decrease;
         }

      }
   }
}
