package me.pollos.polloshook.impl.module.player.fastbreak;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.block.BreakBlockEvent;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.mode.MineMode;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ListenerBlockChange extends ModuleListener<FastBreak, PacketEvent.Receive<BlockUpdateS2CPacket>> {
   public ListenerBlockChange(FastBreak module) {
      super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
   }

   public void call(PacketEvent.Receive<BlockUpdateS2CPacket> event) {
      if (mc.world != null) {
         BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket)event.getPacket();
         BlockPos packetPos = packet.getPos();
         if (packetPos.equals(((FastBreak)this.module).pos)) {
            if (packet.getState().isAir()) {
               BreakBlockEvent blockEvent = new BreakBlockEvent(((FastBreak)this.module).state.getBlock(), ((FastBreak)this.module).pos);
               PollosHook.getEventBus().dispatch(blockEvent);
               ((FastBreak)this.module).instantTimer.setTime(1000L);
               if (((FastBreak)this.module).mode.getValue() == MineMode.PACKET && !mc.world.getBlockState(((FastBreak)this.module).pos).isAir()) {
                  ((FastBreak)this.module).render = false;
               }
            } else if (!packet.getState().equals(((FastBreak)this.module).state) && ((FastBreak)this.module).mode.getValue() == MineMode.PACKET) {
               PacketUtil.send(((FastBreak)this.module).getAbortPacket());
               ((FastBreak)this.module).softReset();
            }
         }

         if (packet.getPos().equals(((FastBreak)this.module).pos) && packet.getState() == mc.world.getBlockState(((FastBreak)this.module).pos) && ((FastBreak)this.module).shouldAbort && ((FastBreak)this.module).mode.getValue() == MineMode.INSTANT && (Boolean)((FastBreak)this.module).strict.getValue()) {
            PacketUtil.send(new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, ((FastBreak)this.module).pos, Direction.DOWN, PacketUtil.incrementSequence()));
            ((FastBreak)this.module).shouldAbort = false;
         }

      }
   }
}