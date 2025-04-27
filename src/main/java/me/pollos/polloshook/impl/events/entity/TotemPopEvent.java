package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.player.PlayerEntity;

public class TotemPopEvent extends Event {
   private final PlayerEntity player;

   
   public PlayerEntity getPlayer() {
      return this.player;
   }

   
   public TotemPopEvent(PlayerEntity player) {
      this.player = player;
   }
}
