package me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist;

import java.util.Objects;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.misc.GameLoopEvent;

public class ListenerGameLoop extends ModuleListener<Arraylist, GameLoopEvent> {
   public ListenerGameLoop(Arraylist module) {
      super(module, GameLoopEvent.class);
   }

   public void call(GameLoopEvent event) {
      if (((Arraylist)this.module).updateTimer.passed(10L)) {
         Arraylist var10000 = (Arraylist)this.module;
         Objects.requireNonNull(var10000);
         PollosHookThread.submit(var10000::animate);
         ((Arraylist)this.module).updateTimer.reset();
      }

   }
}
