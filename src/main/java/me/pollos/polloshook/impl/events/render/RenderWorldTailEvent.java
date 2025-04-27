package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.render.RenderTickCounter;

public class RenderWorldTailEvent extends Event {
   RenderTickCounter counter;

   
   public RenderTickCounter getCounter() {
      return this.counter;
   }

   
   public void setCounter(RenderTickCounter counter) {
      this.counter = counter;
   }

   
   public RenderWorldTailEvent(RenderTickCounter counter) {
      this.counter = counter;
   }
}
