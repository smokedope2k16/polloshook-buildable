package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends Event {
   private final Entity entity;

   
   public Entity getEntity() {
      return this.entity;
   }

   
   public RenderEntityEvent(Entity entity) {
      this.entity = entity;
   }
}
