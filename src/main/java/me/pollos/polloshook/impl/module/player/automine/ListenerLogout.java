package me.pollos.polloshook.impl.module.player.automine;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerLogout extends ModuleListener<AutoMine, LeaveGameEvent> {
   public ListenerLogout(AutoMine module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((AutoMine)this.module).attackPos = null;
      ((AutoMine)this.module).lastPriority = -1;
      ((AutoMine)this.module).enemies.clear();
   }
}
