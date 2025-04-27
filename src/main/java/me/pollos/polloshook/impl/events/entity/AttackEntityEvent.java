package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.events.StageEvent;
import net.minecraft.entity.Entity;

public class AttackEntityEvent extends StageEvent {
   private final Entity entity;

   public AttackEntityEvent(Entity entity, Stage stage) {
      super(stage);
      this.entity = entity;
   }

   
   public Entity getEntity() {
      return this.entity;
   }
}
