package me.pollos.polloshook.impl.module.misc.swing;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.misc.swing.modes.CancelSwing;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;

public class ListenerSwing extends ModuleListener<Swing, PacketEvent.Send<HandSwingC2SPacket>> {
   public ListenerSwing(Swing module) {
      super(module, PacketEvent.Send.class, HandSwingC2SPacket.class);
   }

   public void call(PacketEvent.Send<HandSwingC2SPacket> event) {
      if (((Swing)this.module).cancelSwing) {
         if (((Swing)this.module).noSwing.getValue() != CancelSwing.NONE) {
            event.setCanceled(true);
         }

         ((Swing)this.module).cancelSwing = false;
      }

   }
}
