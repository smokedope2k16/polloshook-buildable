package me.pollos.polloshook.impl.events.render;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.gui.DrawContext;

public class Render2DEvent extends Event {
   private final DrawContext context;

   
   public DrawContext getContext() {
      return this.context;
   }

   
   public Render2DEvent(DrawContext context) {
      this.context = context;
   }
}
