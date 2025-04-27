package me.pollos.polloshook.impl.module.render.freecam;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.mixins.network.IPlayerInteractEntityC2SPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class ListenerUseEntity extends ModuleListener<Freecam, PacketEvent.Send<PlayerInteractEntityC2SPacket>> {
   public ListenerUseEntity(Freecam module) {
      super(module, PacketEvent.Send.class, PlayerInteractEntityC2SPacket.class);
   }

   public void call(PacketEvent.Send<PlayerInteractEntityC2SPacket> event) {
      if (mc.player != null && mc.world != null) {
         IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)event.getPacket();
         if (packet.getEntityID() == mc.player.getId()) {
            event.setCanceled(true);
         }

      }
   }
}
