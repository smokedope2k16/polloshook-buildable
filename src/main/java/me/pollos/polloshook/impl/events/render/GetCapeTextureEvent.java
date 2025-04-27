package me.pollos.polloshook.impl.events.render;


import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

public class GetCapeTextureEvent {
   final AbstractClientPlayerEntity entity;
   Identifier[] identifiers;

   
   public AbstractClientPlayerEntity getEntity() {
      return this.entity;
   }

   
   public Identifier[] getIdentifiers() {
      return this.identifiers;
   }

   
   public void setIdentifiers(Identifier[] identifiers) {
      this.identifiers = identifiers;
   }

   
   public GetCapeTextureEvent(AbstractClientPlayerEntity entity, Identifier[] identifiers) {
      this.entity = entity;
      this.identifiers = identifiers;
   }
}
