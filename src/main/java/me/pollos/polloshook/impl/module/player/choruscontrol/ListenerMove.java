package me.pollos.polloshook.impl.module.player.choruscontrol;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ListenerMove extends ModuleListener<ChorusControl, PacketEvent.Send<?>> {
   public ListenerMove(ChorusControl module) {
      super(module, PacketEvent.Send.class);
   }

   public void call(PacketEvent.Send<?> event) {
      if (((ChorusControl)this.module).cancel && event.getPacket() instanceof PlayerMoveC2SPacket) {
         event.setCanceled(true);
      }

   }
}
