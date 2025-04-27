package me.pollos.polloshook.impl.events.misc;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.util.Window;

public class ResizeWindowEvent extends Event {
   final Window window;

   
   public Window getWindow() {
      return this.window;
   }

   
   public ResizeWindowEvent(Window window) {
      this.window = window;
   }
}
