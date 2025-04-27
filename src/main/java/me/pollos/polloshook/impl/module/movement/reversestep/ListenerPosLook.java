package me.pollos.polloshook.impl.module.movement.reversestep;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends ModuleListener<ReverseStep, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(ReverseStep module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      ((ReverseStep)this.module).timer.reset();
   }
}
