package me.pollos.polloshook.impl.module.movement.elytrafly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;

public class ListenerPacket extends ModuleListener<ElytraFly, PacketEvent.Send<ClientCommandC2SPacket>> {
   public ListenerPacket(ElytraFly module) {
      super(module, PacketEvent.Send.class, ClientCommandC2SPacket.class);
   }

   public void call(PacketEvent.Send<ClientCommandC2SPacket> event) {
      if (((ClientCommandC2SPacket)event.getPacket()).getMode() == Mode.START_FALL_FLYING) {
         ((ElytraFly)this.module).setChina(!((ElytraFly)this.module).isChina());
      }

   }
}