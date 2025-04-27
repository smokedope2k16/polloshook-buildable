package me.pollos.polloshook.impl.events.network;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.network.ServerInfo;

public class GameJoinEvent extends Event {
   private final ServerInfo server;

   
   public ServerInfo getServer() {
      return this.server;
   }

   
   public GameJoinEvent(ServerInfo server) {
      this.server = server;
   }
}
