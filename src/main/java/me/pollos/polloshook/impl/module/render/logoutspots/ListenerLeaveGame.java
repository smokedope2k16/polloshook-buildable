package me.pollos.polloshook.impl.module.render.logoutspots;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerLeaveGame extends ModuleListener<LogoutSpots, LeaveGameEvent> {
   public ListenerLeaveGame(LogoutSpots module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((LogoutSpots)this.module).spots.clear();
   }
}
