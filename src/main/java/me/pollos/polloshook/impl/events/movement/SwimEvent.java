package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.Entity;

public class SwimEvent extends Event {
   final Entity entity;

   
   public Entity getEntity() {
      return this.entity;
   }

   
   public SwimEvent(Entity entity) {
      this.entity = entity;
   }
}
