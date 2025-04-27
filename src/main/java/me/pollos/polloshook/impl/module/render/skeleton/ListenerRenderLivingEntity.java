package me.pollos.polloshook.impl.module.render.skeleton;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderLivingEntityEvent;
import me.pollos.polloshook.impl.module.render.skeleton.util.CacheConsumerProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerRenderLivingEntity extends ModuleListener<Skeleton, RenderLivingEntityEvent.Pre> {
   public ListenerRenderLivingEntity(Skeleton module) {
      super(module, RenderLivingEntityEvent.Pre.class);
   }

   public void call(RenderLivingEntityEvent.Pre event) {
      LivingEntity var3 = event.getLivingEntity();
      if (var3 instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)var3;
         CacheConsumerProvider provider = new CacheConsumerProvider();
         provider.getCacheConsumer().start();
         RenderMethods.drawEntity(event.getMatrix(), player, mc.getRenderTickCounter().getTickDelta(true), provider, false);
         if (((Skeleton)this.module).vertexes.containsKey(player)) {
            ((Skeleton)this.module).vertexes.replace(player, provider);
         } else {
            ((Skeleton)this.module).vertexes.put(player, provider);
         }
      }

   }
}
