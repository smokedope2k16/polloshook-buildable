package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;

public class LightTextureEvent extends Event {
   private int color;

   
   public int getColor() {
      return this.color;
   }

   
   public void setColor(int color) {
      this.color = color;
   }

   
   public LightTextureEvent(int color) {
      this.color = color;
   }
}
