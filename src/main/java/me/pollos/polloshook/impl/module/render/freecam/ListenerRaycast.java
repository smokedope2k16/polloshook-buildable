package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.world.RaycastEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class ListenerRaycast extends SafeModuleListener<Freecam, RaycastEvent> {
   public ListenerRaycast(Freecam module) {
      super(module, RaycastEvent.class);
   }

   public void safeCall(RaycastEvent event) {
      if (event.getResult() != null) {
         HitResult var3 = event.getResult();
         if (var3 instanceof EntityHitResult) {
            EntityHitResult result = (EntityHitResult)var3;
            if (result.getEntity() instanceof ClientPlayerEntity) {
               double max = Math.max(mc.player.getBlockInteractionRange(), mc.player.getEntityInteractionRange());
               event.setResult(mc.cameraEntity.raycast(max, mc.getRenderTickCounter().getTickDelta(true), false));
            }
         }

      }
   }
}
