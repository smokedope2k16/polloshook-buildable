package me.pollos.polloshook.impl.module.render.worldeditesp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.InteractEvent;

public class ListenerInteractBlock extends ModuleListener<WorldEditESP, InteractEvent> {
   public ListenerInteractBlock(WorldEditESP module) {
      super(module, InteractEvent.class);
   }

   public void call(InteractEvent event) {
      if (((WorldEditESP)this.module).isValid(mc.player.getMainHandStack().getItem())) {
         ((WorldEditESP)this.module).secondBlock = event.getPos();
      }

   }
}
