package me.pollos.polloshook.impl.module.movement.speed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MotionUpdateEvent;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedTypeEnum;

public class ListenerMotion extends ModuleListener<Speed, MotionUpdateEvent> {
   public ListenerMotion(Speed module) {
      super(module, MotionUpdateEvent.class);
   }

   public void call(MotionUpdateEvent event) {
      if (!MovementUtil.anyMovementKeysWASD() && !MovementUtil.anyInputWASD(((Speed)this.module).getMovementInput())) {
         mc.player.setVelocity(0.0D, mc.player.getVelocity().y, 0.0D);
         ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().reset();
      }

      ((SpeedTypeEnum)((Speed)this.module).mode.getValue()).getType().onMotionUpdate(event);
   }
}
