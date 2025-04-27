package me.pollos.polloshook.impl.module.movement.liquidspeed;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends ModuleListener<LiquidSpeed, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(LiquidSpeed module) {
      super(module, PacketEvent.Receive.class, PlayerActionResponseS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      ((LiquidSpeed)this.module).timer.reset();
   }
}
