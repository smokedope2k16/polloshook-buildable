package me.pollos.polloshook.impl.events.item;


import me.pollos.polloshook.api.event.events.Event;

public class SetPearlCooldownEvent extends Event {
   int ticks;

   
   public int getTicks() {
      return this.ticks;
   }

   
   public void setTicks(int ticks) {
      this.ticks = ticks;
   }

   
   public SetPearlCooldownEvent(int ticks) {
      this.ticks = ticks;
   }
}
