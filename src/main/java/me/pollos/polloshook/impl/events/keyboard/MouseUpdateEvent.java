package me.pollos.polloshook.impl.events.keyboard;


import me.pollos.polloshook.api.event.events.Event;

public class MouseUpdateEvent extends Event {
   double x;
   double y;

   
   public double getX() {
      return this.x;
   }

   
   public double getY() {
      return this.y;
   }

   
   public void setX(double x) {
      this.x = x;
   }

   
   public void setY(double y) {
      this.y = y;
   }

   
   public MouseUpdateEvent(double x, double y) {
      this.x = x;
      this.y = y;
   }
}
