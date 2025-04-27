package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

public class ListenerSlotClick extends ModuleListener<NoSlow, PacketEvent.Send<ClickSlotC2SPacket>> {
   public ListenerSlotClick(NoSlow module) {
      super(module, PacketEvent.Send.class, ClickSlotC2SPacket.class);
   }

   public void call(PacketEvent.Send<ClickSlotC2SPacket> event) {
      if ((Boolean)((NoSlow)this.module).ncpStrict.getValue()) {
         if (Managers.getPositionManager().isSneaking()) {
            PacketUtil.sneak(false);
         }

         if (Managers.getPositionManager().isSprinting()) {
            PacketUtil.sprint(false);
         }
      }

   }
}
