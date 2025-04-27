package me.pollos.polloshook.impl.module.combat.autocrystal;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class ListenerBlockUpdate extends ModuleListener<AutoCrystal, PacketEvent.Receive<BlockUpdateS2CPacket>> {
   public ListenerBlockUpdate(AutoCrystal module) {
      super(module, PacketEvent.Receive.class, BlockUpdateS2CPacket.class);
   }

   public void call(PacketEvent.Receive<BlockUpdateS2CPacket> event) {
      if (mc.player != null && mc.world != null) {
         BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket)event.getPacket();
         BlockPos pos = packet.getPos();
         BlockState oldState = mc.world.getBlockState(pos);
         if ((oldState.getBlock() == Blocks.OBSIDIAN || oldState.getBlock() == Blocks.BEDROCK) && packet.getState().isAir()) {
            ((AutoCrystal)this.module).getBlockedPositions().put(pos.down(), System.currentTimeMillis());
         }

      }
   }
}