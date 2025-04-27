package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.particle.Particle;
import net.minecraft.particle.ParticleEffect;

public class ParticleEvent extends Event {
   private final Particle particle;
   private final ParticleEffect effect;

   public ParticleEvent(Particle particle) {
      this.particle = particle;
      this.effect = null;
   }

   public ParticleEvent(ParticleEffect effect) {
      this.effect = effect;
      this.particle = null;
   }

   
   public Particle getParticle() {
      return this.particle;
   }

   
   public ParticleEffect getEffect() {
      return this.effect;
   }
}
