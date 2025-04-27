package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class RenderCapeEvent extends Event {
   private final AbstractClientPlayerEntity player;

   
   public AbstractClientPlayerEntity getPlayer() {
      return this.player;
   }

   
   public RenderCapeEvent(AbstractClientPlayerEntity player) {
      this.player = player;
   }
}
