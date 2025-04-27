package me.pollos.polloshook.impl.module.movement.speed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends ModuleListener<Speed, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(Speed module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      ((Speed)this.module).timer.reset();
   }
}
