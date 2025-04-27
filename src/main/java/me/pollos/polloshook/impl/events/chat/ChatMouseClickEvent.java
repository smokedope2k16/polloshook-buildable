package me.pollos.polloshook.impl.events.chat;


import me.pollos.polloshook.api.event.events.Event;

public class ChatMouseClickEvent extends Event {
   double x;
   double z;

   
   public double getX() {
      return this.x;
   }

   
   public double getZ() {
      return this.z;
   }

   
   public ChatMouseClickEvent(double x, double z) {
      this.x = x;
      this.z = z;
   }
}
