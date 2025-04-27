package me.pollos.polloshook.impl.module.player.yaw;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.player.yaw.mode.YawMode;

public class ListenerUpdate extends ModuleListener<YawLock, UpdateEvent> {
   public ListenerUpdate(YawLock module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (!(Boolean)((YawLock)this.module).noCameraTurn.getValue()) {
         float var10000;
         switch((YawMode)((YawLock)this.module).mode.getValue()) {
         case DEGREE_45:
            var10000 = (float)Math.round(mc.player.getYaw() / 45.0F) * 45.0F % 360.0F;
            break;
         case DEGREE_90:
            var10000 = (float)Math.round(mc.player.getYaw() / 90.0F) * 90.0F % 360.0F;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         float yaw = var10000;
         if (mc.player.hasVehicle()) {
            mc.player.getVehicle().setYaw(yaw);
            mc.player.getVehicle().setHeadYaw(yaw);
         } else {
            mc.player.setYaw(yaw);
            mc.player.setHeadYaw(yaw);
         }
      }
   }
}
