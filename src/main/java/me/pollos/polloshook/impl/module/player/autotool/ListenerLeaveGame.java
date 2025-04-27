package me.pollos.polloshook.impl.module.player.autotool;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;

public class ListenerLeaveGame extends ModuleListener<AutoTool, LeaveGameEvent> {
   public ListenerLeaveGame(AutoTool module) {
      super(module, LeaveGameEvent.class);
   }

   public void call(LeaveGameEvent event) {
      ((AutoTool)this.module).reset();
   }
}
