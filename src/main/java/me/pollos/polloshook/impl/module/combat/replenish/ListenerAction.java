package me.pollos.polloshook.impl.module.combat.replenish;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class ListenerAction extends ModuleListener<Replenish, PacketEvent.Send<ClientCommandC2SPacket>> {
   public ListenerAction(Replenish module) {
      super(module, PacketEvent.Send.class, ClientCommandC2SPacket.class);
   }

   public void call(PacketEvent.Send<ClientCommandC2SPacket> event) {
      Mode m = ((ClientCommandC2SPacket)event.getPacket()).getMode();
      ((Replenish)this.module).isServerInv = m == Mode.OPEN_INVENTORY;
   }
}