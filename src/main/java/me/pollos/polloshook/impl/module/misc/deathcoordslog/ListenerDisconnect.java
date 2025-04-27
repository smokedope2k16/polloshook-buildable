package me.pollos.polloshook.impl.module.misc.deathcoordslog;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerDisconnect extends ModuleListener<DeathCoordsLog, LeaveGameEvent> {
   public ListenerDisconnect(DeathCoordsLog module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((DeathCoordsLog)this.module).waypointList.clear();
   }
}
