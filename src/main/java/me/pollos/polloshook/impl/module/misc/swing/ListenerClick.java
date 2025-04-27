package me.pollos.polloshook.impl.module.misc.swing;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

public class ListenerClick extends ModuleListener<Swing, PacketEvent.Send<ClickSlotC2SPacket>> {
   public ListenerClick(Swing module) {
      super(module, PacketEvent.Send.class, ClickSlotC2SPacket.class);
   }

   public void call(PacketEvent.Send<ClickSlotC2SPacket> event) {
      if (mc.world != null) {
         ClickSlotC2SPacket packet = (ClickSlotC2SPacket)event.getPacket();
         if (packet.getSlot() == -999 && packet.getActionType() == SlotActionType.PICKUP && !mc.world.isClient()) {
            ((Swing)this.module).cancelSwing = true;
         }

      }
   }
}
