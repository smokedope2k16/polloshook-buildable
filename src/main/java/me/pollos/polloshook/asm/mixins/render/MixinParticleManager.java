package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.render.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ParticleManager.class})
public class MixinParticleManager {
   @Inject(
      method = {"addParticle(Lnet/minecraft/client/particle/Particle;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void addParticleHook(Particle particle, CallbackInfo ci) {
      ParticleEvent particleEvent = new ParticleEvent(particle);
      PollosHook.getEventBus().dispatch(particleEvent);
      if (particleEvent.isCanceled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void addParticleHook(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
      ParticleEvent particleEvent = new ParticleEvent(parameters);
      PollosHook.getEventBus().dispatch(particleEvent);
      if (particleEvent.isCanceled()) {
         cir.setReturnValue((Particle)null);
      }

   }
}
