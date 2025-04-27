package me.pollos.polloshook.impl.module.player.fastbreak;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.SwapMode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Formatting;

public class ListenerSwap extends ModuleListener<FastBreak, PacketEvent.Post<UpdateSelectedSlotC2SPacket>> {
   public ListenerSwap(FastBreak module) {
      super(module, PacketEvent.Post.class, UpdateSelectedSlotC2SPacket.class);
   }

   public void call(PacketEvent.Post<UpdateSelectedSlotC2SPacket> event) {
      if (((FastBreak)this.module).pos != null && (Boolean)((FastBreak)this.module).strict.getValue() && ((FastBreak)this.module).swap.getValue() != SwapMode.SILENT && Managers.getInventoryManager().getLastSlot() != Managers.getInventoryManager().getSlot()) {
         PacketUtil.send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, ((FastBreak)this.module).pos, ((FastBreak)this.module).direction));
         ((FastBreak)this.module).softReset();
         ((FastBreak)this.module).render = false;
         ((FastBreak)this.module).debugLog(String.valueOf(Formatting.RED) + "SoftReset");
      }

   }
}