package me.pollos.polloshook.impl.module.render.logoutspots;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.network.ConnectionEvent;
import me.pollos.polloshook.impl.module.render.logoutspots.point.LogoutPoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class ListenerJoin extends ModuleListener<LogoutSpots, ConnectionEvent.Join> {
   public ListenerJoin(LogoutSpots module) {
      super(module, ConnectionEvent.Join.class);
   }

   public void call(ConnectionEvent.Join event) {
      if (!event.getName().equals(mc.getSession().getUsername())) {
         LogoutPoint spot = (LogoutPoint)((LogoutSpots)this.module).spots.remove(event.getUuid());
         if ((Boolean)((LogoutSpots)this.module).greeter.getValue()) {
            String text;
            if (spot != null) {
               Vec3d pos = spot.rounded();
               String var10000 = event.getName();
               text = var10000 + " is back at: " + pos.x + ", " + pos.y + ", " + pos.z + ".";
            } else {
               PlayerEntity player = event.getPlayer();
               if (player == null) {
                  return;
               }

               text = player.getName().getString() + " joined at: %s, %s, %s.";
               text = String.format(text, (int)player.getX(), (int)player.getY(), (int)player.getZ());
            }

            ClientLogger var6 = ClientLogger.getLogger();
            String var10001 = String.valueOf(Formatting.WHITE);
            var6.log(var10001 + text);
         }

      }
   }
}
