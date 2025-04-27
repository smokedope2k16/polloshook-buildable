package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.LivingEntity;

public class DeathEvent extends Event {
   private final LivingEntity entity;

   
   public LivingEntity getEntity() {
      return this.entity;
   }

   
   public DeathEvent(LivingEntity entity) {
      this.entity = entity;
   }
}
