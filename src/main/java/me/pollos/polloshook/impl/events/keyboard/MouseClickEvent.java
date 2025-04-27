package me.pollos.polloshook.impl.events.keyboard;


import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.util.binds.mouse.MouseButton;
import me.pollos.polloshook.api.util.binds.mouse.MouseClickAction;

public class MouseClickEvent extends Event {
   private final MouseButton key;
   private final MouseClickAction action;

   
   public MouseButton getKey() {
      return this.key;
   }

   
   public MouseClickAction getAction() {
      return this.action;
   }

   
   public MouseClickEvent(MouseButton key, MouseClickAction action) {
      this.key = key;
      this.action = action;
   }
}
