package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.movement.MoveEvent;

public class ListenerMove extends ModuleListener<NoSlow, MoveEvent> {
   public ListenerMove(NoSlow module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      if (((NoSlow)this.module).isInWeb()) {
         event.setY(event.getY() - (double)((Float)((NoSlow)this.module).webSpeed.getValue() / 10.0F));
      }

   }
}
