package me.pollos.polloshook.impl.module.render.norender;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.ParticleEvent;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.SpellParticle;

public class ListenerParticle extends ModuleListener<NoRender, ParticleEvent> {
   public ListenerParticle(NoRender module) {
      super(module, ParticleEvent.class);
   }

   public void call(ParticleEvent event) {
      if ((Boolean)((NoRender)this.module).getExplosion().getValue() && (event.getParticle() instanceof ExplosionLargeParticle || event.getParticle() instanceof ExplosionSmokeParticle)) {
         event.setCanceled(true);
      }

      if ((Boolean)((NoRender)this.module).getSmoke().getValue() && (event.getParticle() instanceof FireSmokeParticle || event.getParticle() instanceof ExplosionSmokeParticle)) {
         event.setCanceled(true);
      }

      if ((Boolean)((NoRender)this.module).getEffectParticles().getValue() && event.getParticle() instanceof SpellParticle) {
         event.setCanceled(true);
      }

   }
}
