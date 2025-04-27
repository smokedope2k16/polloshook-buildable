package me.pollos.polloshook.impl.events.gui;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.gui.screen.Screen;

public class ScreenEvent extends Event {
   private final Screen screen;

   
   public Screen getScreen() {
      return this.screen;
   }

   
   public ScreenEvent(Screen screen) {
      this.screen = screen;
   }
}
