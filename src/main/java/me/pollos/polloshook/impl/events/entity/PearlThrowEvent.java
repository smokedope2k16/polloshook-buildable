package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

public class PearlThrowEvent extends Event {
   private final PlayerEntity thrower;
   private final EnderPearlEntity pearl;

   
   public PlayerEntity getThrower() {
      return this.thrower;
   }

   
   public EnderPearlEntity getPearl() {
      return this.pearl;
   }

   
   public PearlThrowEvent(PlayerEntity thrower, EnderPearlEntity pearl) {
      this.thrower = thrower;
      this.pearl = pearl;
   }
}
