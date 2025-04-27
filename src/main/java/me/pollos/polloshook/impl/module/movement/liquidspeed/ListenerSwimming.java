package me.pollos.polloshook.impl.module.movement.liquidspeed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.impl.events.movement.SwimEvent;
import net.minecraft.client.network.ClientPlayerEntity;

public class ListenerSwimming extends ModuleListener<LiquidSpeed, SwimEvent> {
   public ListenerSwimming(LiquidSpeed module) {
      super(module, SwimEvent.class);
   }

   public void call(SwimEvent event) {
      if ((Boolean)((LiquidSpeed)this.module).cancelSwimming.getValue() && ((LiquidSpeed)this.module).cancelSwimming.getParent().isVisible() && event.getEntity() instanceof ClientPlayerEntity && PlayerUtil.isInWater()) {
         event.setCanceled(true);
      }

   }
}
