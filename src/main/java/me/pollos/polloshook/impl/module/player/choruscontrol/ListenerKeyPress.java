package me.pollos.polloshook.impl.module.player.choruscontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.keyboard.KeyPressEvent;

public class ListenerKeyPress extends ModuleListener<ChorusControl, KeyPressEvent> {
   public ListenerKeyPress(ChorusControl module) {
      super(module, KeyPressEvent.class);
   }

   public void call(KeyPressEvent event) {
      if (event.getKey() == 340) {
         ((ChorusControl)this.module).sendPacket();
      }

   }
}
