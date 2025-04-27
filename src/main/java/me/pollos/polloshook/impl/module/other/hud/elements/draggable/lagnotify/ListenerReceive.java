package me.pollos.polloshook.impl.module.other.hud.elements.draggable.lagnotify;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;

public class ListenerReceive extends ModuleListener<LagNotify, PacketEvent.Receive<?>> {
   public ListenerReceive(LagNotify module) {
      super(module, PacketEvent.Receive.class);
   }

   public void call(PacketEvent.Receive<?> event) {
      if (!(event.getPacket() instanceof PingResultS2CPacket)) {
         ((LagNotify)this.module).timer.reset();
      }
   }
}
