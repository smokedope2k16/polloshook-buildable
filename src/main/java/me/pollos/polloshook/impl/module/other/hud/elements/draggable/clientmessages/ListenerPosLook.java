package me.pollos.polloshook.impl.module.other.hud.elements.draggable.clientmessages;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends SafeModuleListener<ClientMessages, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(ClientMessages module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void safeCall(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      if ((Boolean)((ClientMessages)this.module).lagbacks.getValue()) {
         ((ClientMessages)this.module).displayMessage("received server lagback packet");
      }

   }
}
