package me.pollos.polloshook.impl.module.player.mutlitask;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerMultiTask extends ModuleListener<MultiTask, MultiTask.MultiTaskEvent> {
   public ListenerMultiTask(MultiTask module) {
      super(module, MultiTask.MultiTaskEvent.class);
   }

   public void call(MultiTask.MultiTaskEvent event) {
      event.setCanceled(true);
   }
}
