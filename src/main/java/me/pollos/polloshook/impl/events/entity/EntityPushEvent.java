package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.Entity;

public class EntityPushEvent extends Event {
   private final Entity entity;

   
   public Entity getEntity() {
      return this.entity;
   }

   
   public EntityPushEvent(Entity entity) {
      this.entity = entity;
   }
}
