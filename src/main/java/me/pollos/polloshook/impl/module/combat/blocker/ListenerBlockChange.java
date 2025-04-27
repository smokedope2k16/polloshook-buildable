package me.pollos.polloshook.impl.module.combat.blocker;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockChange extends ModuleListener<Blocker, PacketEvent.Receive<BlockUpdateS2CPacket>> {
   public ListenerBlockChange(Blocker module) {
      super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
   }

   public void call(PacketEvent.Receive<BlockUpdateS2CPacket> event) {
      if (mc.world != null) {
         if ((Boolean)((Blocker)this.module).blockUpdate.getValue()) {
            BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket)event.getPacket();
            BlockPos pos = packet.getPos();
            FastBreak FAST_BREAK = (FastBreak)Managers.getModuleManager().get(FastBreak.class);
            if (packet.getState().getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN && !((Blocker)this.module).minePositions.containsKey(pos)) {
               if (pos.equals(FAST_BREAK.getPos())) {
                  return;
               }

               ((Blocker)this.module).minePositions.put(packet.getPos(), System.currentTimeMillis());
            }
         }

      }
   }
}