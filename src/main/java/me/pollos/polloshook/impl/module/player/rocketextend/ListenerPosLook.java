package me.pollos.polloshook.impl.module.player.rocketextend;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends ModuleListener<FireworkExtend, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(FireworkExtend module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      ((FireworkExtend)this.module).timer.reset();
      ((FireworkExtend)this.module).removeRocket();
   }
}
