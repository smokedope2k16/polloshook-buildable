package me.pollos.polloshook.impl.module.other.window;

import java.util.Objects;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<WindowModule, TickEvent> {
   public ListenerTick(WindowModule module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      WindowModule var10002 = (WindowModule)this.module;
      Objects.requireNonNull(var10002);
      Thread thread = new Thread(var10002::apply, "Update Window Thread");
      thread.start();
   }
}
