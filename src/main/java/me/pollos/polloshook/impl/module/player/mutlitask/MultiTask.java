package me.pollos.polloshook.impl.module.player.mutlitask;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class MultiTask extends ToggleableModule {
   public MultiTask() {
      super(new String[]{"MultiTask", "multitasking", "task"}, Category.PLAYER);
      this.offerListeners(new Listener[]{new ListenerMultiTask(this)});
   }

   public static class MultiTaskEvent extends Event {
   }
}
