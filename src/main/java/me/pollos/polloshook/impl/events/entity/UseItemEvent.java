package me.pollos.polloshook.impl.events.entity;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.util.Hand;

public class UseItemEvent extends Event {
   private final Hand hand;

   
   public Hand getHand() {
      return this.hand;
   }

   
   public UseItemEvent(Hand hand) {
      this.hand = hand;
   }
}
