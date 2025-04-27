package me.pollos.polloshook.impl.module.player.choruscontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends ModuleListener<ChorusControl, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(ChorusControl module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      if (((ChorusControl)this.module).cancel) {
         ((ChorusControl)this.module).packet = (PlayerPositionLookS2CPacket)event.getPacket();
         event.setCanceled(true);
      }

   }
}
