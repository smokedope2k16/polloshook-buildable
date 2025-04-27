package me.pollos.polloshook.impl.module.combat.autocrystal;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;

public class ListenerGameLoop extends ModuleListener<AutoCrystal, GameLoopEvent> {
   public ListenerGameLoop(AutoCrystal module) {
      super(module, GameLoopEvent.class);
   }

   public void call(GameLoopEvent event) {
      ((AutoCrystal)this.module).initThreads();
   }
}
