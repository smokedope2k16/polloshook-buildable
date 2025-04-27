package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.Entity;

public class EntityOutlineEvent extends Event {
   private final Entity entity;

   
   public EntityOutlineEvent(Entity entity) {
      this.entity = entity;
   }

   
   public Entity getEntity() {
      return this.entity;
   }
}
