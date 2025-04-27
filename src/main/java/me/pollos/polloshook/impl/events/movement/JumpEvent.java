package me.pollos.polloshook.impl.events.movement;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.player.PlayerEntity;

public class JumpEvent extends Event {
   private final PlayerEntity player;

   
   public PlayerEntity getPlayer() {
      return this.player;
   }

   
   public JumpEvent(PlayerEntity player) {
      this.player = player;
   }
}
