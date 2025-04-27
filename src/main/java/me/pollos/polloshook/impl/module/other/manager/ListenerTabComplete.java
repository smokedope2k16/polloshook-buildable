package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

public class ListenerTabComplete extends ModuleListener<Manager, PacketEvent.Send<RequestCommandCompletionsC2SPacket>> {
   public ListenerTabComplete(Manager module) {
      super(module, PacketEvent.Send.class, RequestCommandCompletionsC2SPacket.class);
   }

   public void call(PacketEvent.Send<RequestCommandCompletionsC2SPacket> event) {
      String prefix = Managers.getCommandManager().getPrefix();
      RequestCommandCompletionsC2SPacket packet = (RequestCommandCompletionsC2SPacket)event.getPacket();
      if (packet.getPartialCommand().startsWith(prefix)) {
         event.setCanceled(true);
      }

   }
}
