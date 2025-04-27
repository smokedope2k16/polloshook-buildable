package me.pollos.polloshook.impl.events.keyboard;


import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyPressAction;

public class KeyPressEvent extends Event {
   private final int key;
   private final KeyPressAction action;

   
   public int getKey() {
      return this.key;
   }

   
   public KeyPressAction getAction() {
      return this.action;
   }

   
   public KeyPressEvent(int key, KeyPressAction action) {
      this.key = key;
      this.action = action;
   }
}
