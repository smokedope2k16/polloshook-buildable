package me.pollos.polloshook.impl.events.item;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.input.Input;

public class InputUpdateEvent extends Event {
   Input input;

   
   public Input getInput() {
      return this.input;
   }

   
   public InputUpdateEvent(Input input) {
      this.input = input;
   }
}
