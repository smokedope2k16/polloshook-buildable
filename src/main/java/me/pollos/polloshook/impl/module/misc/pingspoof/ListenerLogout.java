package me.pollos.polloshook.impl.module.misc.pingspoof;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerLogout extends ModuleListener<PingSpoof, LeaveGameEvent> {
   public ListenerLogout(PingSpoof module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((PingSpoof)this.module).clearPackets(false);
   }
}
