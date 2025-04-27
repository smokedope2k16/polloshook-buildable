package me.pollos.polloshook.impl.module.misc.pingspoof;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;

public class ListenerPacket extends ModuleListener<PingSpoof, PacketEvent.Send<KeepAliveC2SPacket>> {
   public ListenerPacket(PingSpoof module) {
      super(module, PacketEvent.Send.class, KeepAliveC2SPacket.class);
   }

   public void call(PacketEvent.Send<KeepAliveC2SPacket> event) {
      KeepAliveC2SPacket packet = (KeepAliveC2SPacket)event.getPacket();
      if (!((PingSpoof)this.module).packets.remove(packet)) {
         ((PingSpoof)this.module).onPacket(packet);
         event.setCanceled(true);
      }
   }
}
