package me.pollos.polloshook.impl.module.combat.blocker;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.util.math.BlockPos;

public class ListenerAnimation extends ModuleListener<Blocker, PacketEvent.Receive<BlockBreakingProgressS2CPacket>> {
   public ListenerAnimation(Blocker module) {
      super(module, PacketEvent.Receive.class, BlockBreakingProgressS2CPacket.class);
   }

   public void call(PacketEvent.Receive<BlockBreakingProgressS2CPacket> event) {
      if (mc.world != null) {
         BlockBreakingProgressS2CPacket packet = (BlockBreakingProgressS2CPacket)event.getPacket();
         BlockPos pos = packet.getPos();
         BlockState state = mc.world.getBlockState(pos);
         if (state.getBlock() == Blocks.OBSIDIAN && !((Blocker)this.module).minePositions.containsKey(pos)) {
            if (this.ignoreFriend(packet.getEntityId())) {
               return;
            }

            ((Blocker)this.module).minePositions.put(packet.getPos(), System.currentTimeMillis());
         }

      }
   }

   private boolean ignoreFriend(int id) {
      Entity entity = mc.world.getEntityById(id);
      if (entity == null) {
         return false;
      } else if (entity instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)entity;
         return Managers.getFriendManager().isFriend(player);
      } else {
         return false;
      }
   }
}
