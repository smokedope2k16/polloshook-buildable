package me.pollos.polloshook.impl.module.misc.nobreakanim;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.block.MineUtil;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;

public class ListenerBreak extends ModuleListener<NoBreakAnim, PacketEvent.Send<PlayerActionC2SPacket>> {
   public ListenerBreak(NoBreakAnim module) {
      super(module, PacketEvent.Send.class, PlayerActionC2SPacket.class);
   }

   public void call(PacketEvent.Send<PlayerActionC2SPacket> event) {
      if (!PlayerUtil.isCreative() && ((PlayerActionC2SPacket)event.getPacket()).getAction() == Action.START_DESTROY_BLOCK && MineUtil.canBreak(((PlayerActionC2SPacket)event.getPacket()).getPos())) {
         PacketUtil.send(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, ((PlayerActionC2SPacket)event.getPacket()).getPos(), ((PlayerActionC2SPacket)event.getPacket()).getDirection()));
      }
   }
}