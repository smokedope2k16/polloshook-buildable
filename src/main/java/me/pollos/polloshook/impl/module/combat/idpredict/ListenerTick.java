package me.pollos.polloshook.impl.module.combat.idpredict;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.TickEvent;

public class ListenerTick extends ModuleListener<IDPredict, TickEvent> {
   public ListenerTick(IDPredict module) {
      super(module, TickEvent.class);
   }

   public void call(TickEvent event) {
      if (((IDPredict)this.module).attackTimer.passed(1000L)) {
         ((IDPredict)this.module).attacks = 0;
      }

      if (mc.world != null && !((IDPredict)this.module).isUpdated()) {
         ((IDPredict)this.module).update();
         ((IDPredict)this.module).setUpdated(true);
      }

   }
}
