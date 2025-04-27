package me.pollos.polloshook.impl.module.player.nointerp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.EntityInterpolationEvent;
import net.minecraft.entity.Entity;

public class ListenerInterp extends ModuleListener<NoInterpolation, EntityInterpolationEvent> {
   public ListenerInterp(NoInterpolation module) {
      super(module, EntityInterpolationEvent.class);
   }

   public void call(EntityInterpolationEvent event) {
      Entity entity = event.getEntity();
      entity.setPos(event.getX(), event.getY(), event.getZ());
      entity.setYaw(event.getYaw());
      entity.setPitch(event.getPitch());
   }
}
