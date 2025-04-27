package me.pollos.polloshook.impl.module.misc.swing;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.swing.modes.CancelSwing;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

public class ListenerDrop extends ModuleListener<Swing, PacketEvent.Send<PlayerActionC2SPacket>> {
   public ListenerDrop(Swing module) {
      super(module, PacketEvent.Send.class, PlayerActionC2SPacket.class);
   }

   public void call(PacketEvent.Send<PlayerActionC2SPacket> event) {
      PlayerActionC2SPacket packet = (PlayerActionC2SPacket)event.getPacket();
      Action action = packet.getAction();
      if (((Swing)this.module).noSwing.getValue() != CancelSwing.NONE && (action == Action.DROP_ITEM || action == Action.DROP_ALL_ITEMS)) {
         ((Swing)this.module).cancelSwing = true;
      }

   }
}