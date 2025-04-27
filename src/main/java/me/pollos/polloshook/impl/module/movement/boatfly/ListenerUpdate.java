package me.pollos.polloshook.impl.module.movement.boatfly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;

public class ListenerUpdate extends ModuleListener<BoatFly, UpdateEvent> {
   protected boolean fix = false;

   public ListenerUpdate(BoatFly module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      this.fix = KeyboardUtil.isPressed(mc.options.sprintKey.getDefaultKey().getCode());
      if (((BoatFly)this.module).isValid(mc.player.getVehicle())) {
         if ((Boolean)((BoatFly)this.module).fixYaw.getValue()) {
            mc.player.getVehicle().setBodyYaw(mc.player.getYaw());
            mc.player.getVehicle().setHeadYaw(mc.player.getYaw());
            mc.player.getVehicle().setYaw(mc.player.getYaw());
         }

         MovementUtil.setYVelocity(this.getYSpeed(), mc.player.getVehicle());
      }

   }

   private double getYSpeed() {
      if (mc.player.input.jumping) {
         return (double)(Float)((BoatFly)this.module).upSpeed.getValue();
      } else if (this.fix) {
         return (double)(-(Float)((BoatFly)this.module).downSpeed.getValue());
      } else {
         return (Boolean)((BoatFly)this.module).glide.getValue() ? (double)(-(Float)((BoatFly)this.module).glideSpeed.getValue()) : 0.0D;
      }
   }
}
