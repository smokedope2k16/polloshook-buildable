package me.pollos.polloshook.impl.module.movement.fly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.impl.events.movement.MoveEvent;

public class ListenerMove extends ModuleListener<Fly, MoveEvent> {
   public ListenerMove(Fly module) {
      super(module, MoveEvent.class);
   }

   public void call(MoveEvent event) {
      float horizontalSpeed = (Float)((Fly)this.module).horizontal.getValue() / 10.0F;
      float verticalSpeed = (Float)((Fly)this.module).vertical.getValue() / 10.0F;
      if (mc.player.input.jumping) {
         event.setY((double)verticalSpeed);
      } else if (mc.player.input.sneaking) {
         event.setY((double)(-verticalSpeed));
      } else {
         event.setY(0.0D);
         if (!mc.player.verticalCollision && (Boolean)((Fly)this.module).glide.getValue()) {
            event.setY(event.getY() - (double)(Float)((Fly)this.module).factor.getValue());
         }
      }

      MovementUtil.strafe(event, (double)horizontalSpeed);
   }
}
