package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.Entity;

public class EntityOutlineColorEvent extends Event {
   private final Entity entity;
   private int color;

   
   public Entity getEntity() {
      return this.entity;
   }

   
   public int getColor() {
      return this.color;
   }

   
   public void setColor(int color) {
      this.color = color;
   }

   
   public EntityOutlineColorEvent(Entity entity) {
      this.entity = entity;
   }
}
