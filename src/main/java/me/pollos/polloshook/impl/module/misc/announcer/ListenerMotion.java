package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.events.Stage;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;

public class ListenerMotion extends ModuleListener<Announcer, MotionUpdateEvent> {
   public ListenerMotion(Announcer module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (event.getStage() == Stage.PRE && (Boolean)((Announcer)this.module).walk.getValue()) {
         Announcer var10000 = (Announcer)this.module;
         var10000.walkDistance += (float)MovementUtil.getDistance2D();
      }

   }
}
