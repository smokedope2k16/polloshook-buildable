package me.pollos.polloshook.impl.manager.minecraft.connection;

import java.util.Iterator;
import java.util.UUID;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.impl.events.network.ConnectionEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;

public class ConnectionManager extends SubscriberImpl {
   public ConnectionManager() {
      this.listeners.add(new Listener<PacketEvent.Receive<PlayerListS2CPacket>>(
          PacketEvent.Receive.class,
          Integer.MAX_VALUE,       
          PlayerListS2CPacket.class 
      ) {
         @Override
         public void call(PacketEvent.Receive<PlayerListS2CPacket> event) {
            if (mc.world != null) {
               PlayerListS2CPacket packet = event.getPacket();

               try {
                  Iterator<Entry> var3 = packet.getEntries().iterator();

                  while(var3.hasNext()) {
                     Entry entry = var3.next();
                     Iterator<Action> var5 = packet.getActions().iterator();

                     while(var5.hasNext()) {
                        Action action = var5.next();
                        if (action == Action.ADD_PLAYER) {
                           PlayerEntity playerEntity = mc.world.getPlayerByUuid(entry.profile().getId());
                           PollosHook.getEventBus().dispatch(new ConnectionEvent.Join(
                               entry.profile().getName(),
                               entry.profile().getId(),
                               playerEntity
                           ));
                        }
                     }
                  }
               } catch (Exception var7) {
                  var7.printStackTrace();
               }
            }
         }
      });

      this.listeners.add(new Listener<PacketEvent.Receive<PlayerRemoveS2CPacket>>(
          PacketEvent.Receive.class, 
          Integer.MAX_VALUE,        
          PlayerRemoveS2CPacket.class
      ) {
         @Override
         public void call(PacketEvent.Receive<PlayerRemoveS2CPacket> event) {
            if (mc.world != null) {
               PlayerRemoveS2CPacket packet = event.getPacket();

               for (UUID uuid : packet.profileIds()) {
                  try {
                     PlayerEntity entityByUUID = mc.world.getPlayerByUuid(uuid);
                     if (entityByUUID != null) {
                         PollosHook.getEventBus().dispatch(new ConnectionEvent.Leave(
                             entityByUUID.getName().getString(),
                             uuid,
                             entityByUUID
                         ));
                     } else {
                     }
                  } catch (Exception var6) {
                     var6.printStackTrace();
                  }
               }
            }
         }
      });
   }
}