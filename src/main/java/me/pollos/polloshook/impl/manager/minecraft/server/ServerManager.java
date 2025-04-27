package me.pollos.polloshook.impl.manager.minecraft.server;

import java.util.concurrent.TimeUnit;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.thread.PollosHookThread;
import me.pollos.polloshook.impl.events.network.GameJoinEvent;
import me.pollos.polloshook.impl.events.network.LeaveGameEvent;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.Items;

public class ServerManager extends SubscriberImpl implements Minecraftable {
   private ServerInfo lastServer;

   public ServerManager() {
      this.getListeners().add(new Listener<GameJoinEvent>(GameJoinEvent.class) {
         public void call(GameJoinEvent event) {
            ServerManager.this.lastServer = event.getServer();
         }
      });
      this.getListeners().add(new Listener<LeaveGameEvent>(LeaveGameEvent.class) {
         public void call(LeaveGameEvent event) {
            if (!PlayerUtil.isNull()) {
               if (mc.getServer() != null) {
                  ClientLogger.getLogger().info("Logged out on server [%s] at [%s] with [%s] totems remaining".formatted(new Object[]{mc.getServer().getServerIp(), mc.player.getBlockPos().toShortString(), InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING)}));
               }
            }
         }
      });
   }

   public void reconnectToLastServer() {
      if (this.lastServer != null) {
         PollosHookThread.SCHEDULED_EXECUTOR.schedule(() -> {
            mc.execute(() -> {
               NetworkUtil.connectToServer(this.lastServer);
            });
         }, 100L, TimeUnit.MILLISECONDS);
      }
   }

   
   public ServerInfo getLastServer() {
      return this.lastServer;
   }
}