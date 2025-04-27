package me.pollos.polloshook.impl.module.movement.longjump;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.movement.MoveEvent;
import me.pollos.polloshook.impl.module.movement.longjump.type.mode.LongJumpMode;

public class ListenerMove extends ModuleListener<LongJump, MoveEvent> {
   public ListenerMove(LongJump module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      if (((LongJump)this.module).canLongJump()) {
         if (((LongJumpMode)((LongJump)this.module).mode.getValue()).isNotFuckingSatanMode((Enum)((LongJump)this.module).mode.getValue())) {
            ((LongJumpMode)((LongJump)this.module).mode.getValue()).getType().move(event);
         }

      }
   }
}
