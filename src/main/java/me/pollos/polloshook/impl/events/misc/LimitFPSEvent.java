package me.pollos.polloshook.impl.events.misc;


import me.pollos.polloshook.api.event.events.Event;

public class LimitFPSEvent extends Event {
   private int fps;

   
   public int getFps() {
      return this.fps;
   }

   
   public void setFps(int fps) {
      this.fps = fps;
   }
}
