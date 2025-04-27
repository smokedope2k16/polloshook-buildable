package me.pollos.polloshook.impl.module.render.chams;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerLogout extends ModuleListener<Chams, LeaveGameEvent> {
   public ListenerLogout(Chams module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((Chams)this.module).renderings.clear();
      ((Chams)this.module).vertexes.clear();
   }
}
