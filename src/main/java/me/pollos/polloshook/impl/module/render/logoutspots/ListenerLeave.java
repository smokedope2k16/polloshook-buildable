package me.pollos.polloshook.impl.module.render.logoutspots;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.impl.events.network.ConnectionEvent;
import me.pollos.polloshook.impl.module.render.logoutspots.point.LogoutPoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class ListenerLeave extends ModuleListener<LogoutSpots, ConnectionEvent.Leave> {
   public ListenerLeave(LogoutSpots module) {
      super(module, ConnectionEvent.Leave.class);
   }

   public void call(ConnectionEvent.Leave event) {
      PlayerEntity player = event.getPlayer();
      if ((Boolean)((LogoutSpots)this.module).greeter.getValue()) {
         String text = null;
         if (player != null) {
            text = String.format(player.getName().getString() + " logged out at: %s, %s, %s.", (int)player.getX(), (int)player.getY(), (int)player.getZ());
         }

         if (text != null) {
            ClientLogger var10000 = ClientLogger.getLogger();
            String var10001 = String.valueOf(Formatting.WHITE);
            var10000.log(var10001 + text);
         }
      }

      if (player != null) {
         LogoutPoint spot = new LogoutPoint(player);
         ((LogoutSpots)this.module).spots.put(player.getUuid(), spot);
      }

   }
}
