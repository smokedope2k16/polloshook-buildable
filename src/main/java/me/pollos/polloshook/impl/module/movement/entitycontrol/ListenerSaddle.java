package me.pollos.polloshook.impl.module.movement.entitycontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerSaddle extends ModuleListener<EntityControl, EntityControl.SaddleEvent> {
   public ListenerSaddle(EntityControl module) {
      super(module, EntityControl.SaddleEvent.class);
   }

   public void call(EntityControl.SaddleEvent event) {
      if ((Boolean)((EntityControl)this.module).control.getValue()) {
         event.setCanceled(true);
      }

   }
}
