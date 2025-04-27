package me.pollos.polloshook.impl.module.combat.idpredict;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class ListenerSwitch extends ModuleListener<IDPredict, PacketEvent.Post<UpdateSelectedSlotC2SPacket>> {
   public ListenerSwitch(IDPredict module) {
      super(module, PacketEvent.Post.class, UpdateSelectedSlotC2SPacket.class);
   }

   public void call(PacketEvent.Post<UpdateSelectedSlotC2SPacket> event) {
      ((IDPredict)this.module).setSlot(((UpdateSelectedSlotC2SPacket)event.getPacket()).getSelectedSlot());
   }
}
