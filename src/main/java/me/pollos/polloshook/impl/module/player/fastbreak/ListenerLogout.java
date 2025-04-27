package me.pollos.polloshook.impl.module.player.fastbreak;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerLogout extends ModuleListener<FastBreak, LeaveGameEvent> {
   public ListenerLogout(FastBreak module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((FastBreak)this.module).reset();
   }
}
