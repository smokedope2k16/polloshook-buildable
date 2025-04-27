package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;

public class EntityWorldEvent extends Event {
   private final Entity entity;

   public EntityWorldEvent(Entity entity) {
      this.entity = entity;
   }

   
   public Entity getEntity() {
      return this.entity;
   }

   public static class Remove extends EntityWorldEvent {
      private final RemovalReason reason;

      public Remove(Entity entity, RemovalReason reason) {
         super(entity);
         this.reason = reason;
      }

      
      public RemovalReason getReason() {
         return this.reason;
      }
   }

   public static class Add extends EntityWorldEvent {
      public Add(Entity entity) {
         super(entity);
      }
   }
}
