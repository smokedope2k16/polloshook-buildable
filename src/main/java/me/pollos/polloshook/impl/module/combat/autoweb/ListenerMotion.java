package me.pollos.polloshook.impl.module.combat.autoweb;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.module.BlockPlaceModule;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;

public class ListenerMotion extends ModuleListener<AutoWeb, MotionUpdateEvent> {
   public ListenerMotion(AutoWeb module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (!((AutoWeb)this.module).handleJump((BlockPlaceModule)this.module)) {
         ((AutoWeb)this.module).posList = ((AutoWeb)this.module).findTargets();
         if (!((AutoWeb)this.module).posList.isEmpty()) {
            ((AutoWeb)this.module).onEvent(((AutoWeb)this.module).posList, event);
         }
      }
   }
}
