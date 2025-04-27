package me.pollos.polloshook.impl.module.movement.longjump;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.movement.longjump.type.mode.LongJumpMode;

public class ListenerTick extends SafeModuleListener<LongJump, TickEvent> {
   public ListenerTick(LongJump module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if (((LongJump)this.module).canLongJump()) {
         if (!((LongJumpMode)((LongJump)this.module).mode.getValue()).isNotFuckingSatanMode((Enum)((LongJump)this.module).mode.getValue())) {
            ((LongJumpMode)((LongJump)this.module).mode.getValue()).getType().move((MoveEvent)null);
         }

      }
   }
}
