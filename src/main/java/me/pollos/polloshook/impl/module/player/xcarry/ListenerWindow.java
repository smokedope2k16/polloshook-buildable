package me.pollos.polloshook.impl.module.player.xcarry;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class ListenerWindow extends ModuleListener<XCarry, PacketEvent.Send<CloseHandledScreenC2SPacket>> {
   public ListenerWindow(XCarry module) {
      super(module, PacketEvent.Send.class, CloseHandledScreenC2SPacket.class);
   }

   public void call(PacketEvent.Send<CloseHandledScreenC2SPacket> event) {
      event.setCanceled((Boolean)((XCarry)this.module).forceCancel.getValue() || ((CloseHandledScreenC2SPacket)event.getPacket()).getSyncId() == 0);
   }
}
