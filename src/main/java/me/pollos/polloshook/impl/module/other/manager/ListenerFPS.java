package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.misc.LimitFPSEvent;

public class ListenerFPS extends ModuleListener<Manager, LimitFPSEvent> {
   public ListenerFPS(Manager module) {
      super(module, LimitFPSEvent.class);
   }

   public void call(LimitFPSEvent event) {
      if ((Boolean)((Manager)this.module).unfocusedCPU.getValue() && !mc.isWindowFocused()) {
         event.setFps(30);
         event.setCanceled(true);
      }

   }
}
