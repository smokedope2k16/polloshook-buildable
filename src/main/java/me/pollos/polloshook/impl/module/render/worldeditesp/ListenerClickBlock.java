package me.pollos.polloshook.impl.module.render.worldeditesp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.block.AttackBlockEvent;

public class ListenerClickBlock extends ModuleListener<WorldEditESP, AttackBlockEvent> {
   public ListenerClickBlock(WorldEditESP module) {
      super(module, AttackBlockEvent.class);
   }

   public void call(AttackBlockEvent event) {
      if (((WorldEditESP)this.module).isValid(mc.player.getMainHandStack().getItem())) {
         ((WorldEditESP)this.module).firstBlock = event.getPos();
      }

   }
}
