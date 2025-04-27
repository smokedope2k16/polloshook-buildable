package me.pollos.polloshook.impl.module.player.rocketextend;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import net.minecraft.entity.projectile.FireworkRocketEntity;

public class ListenerFirework extends ModuleListener<FireworkExtend, FireworkExtend.TickFireworkEvent> {
   public ListenerFirework(FireworkExtend module) {
      super(module, FireworkExtend.TickFireworkEvent.class);
   }

   public void call(FireworkExtend.TickFireworkEvent event) {
      FireworkRocketEntity entity = event.getRocket();
      if (mc.player.isFallFlying() && !mc.player.isOnGround()) {
         ScheduledExecutorService var10000 = PollosHookThread.SCHEDULED_EXECUTOR;
         FireworkExtend var10001 = (FireworkExtend)this.module;
         Objects.requireNonNull(var10001);
         var10000.schedule(var10001::removeRocket, ((Float)((FireworkExtend)this.module).timeout.getValue()).longValue(), TimeUnit.SECONDS);
         ((FireworkExtend)this.module).rocket = entity;
         event.setCanceled(true);
      }

   }
}
