package me.pollos.polloshook.impl.module.combat.autocrystal;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;

public class ListenerMotion extends ModuleListener<AutoCrystal, MotionUpdateEvent> {
   public ListenerMotion(AutoCrystal module) {
      super(module, MotionUpdateEvent.class, 9000);
   }

   public void call(MotionUpdateEvent event) {
      ((AutoCrystal)this.module).onEvent(event);
   }
}
