package me.pollos.polloshook.impl.module.combat.idpredict;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ListenerPlace extends ModuleListener<IDPredict, PacketEvent.Post<PlayerInteractBlockC2SPacket>> {
   public ListenerPlace(IDPredict module) {
      super(module, PacketEvent.Post.class, PlayerInteractBlockC2SPacket.class);
   }

   public void call(PacketEvent.Post<PlayerInteractBlockC2SPacket> event) {
      if (!((IDPredict)this.module).isIgnored()) {
         if (mc.player != null && mc.world != null) {
            if (((IDPredict)this.module).timer.passed((long)(Integer)((IDPredict)this.module).coolDown.getValue())) {
               PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket)event.getPacket();
               BlockPos pos = packet.getBlockHitResult().getBlockPos();
               if (mc.world.getBlockState(pos).getBlock() == Blocks.ANVIL || mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN) {
                  BlockPos up = pos.add(0, 1, 0);
                  if (mc.world.getBlockState(up).getBlock() == Blocks.AIR) {
                     FastBreak FAST_BREAK = (FastBreak)Managers.getModuleManager().get(FastBreak.class);
                     if (FAST_BREAK.getPos() == null || !FAST_BREAK.getPos().equals(pos)) {
                        ItemStack stack = mc.player.getInventory().getStack(((IDPredict)this.module).getSlot());
                        boolean silent = stack.getItem() == Items.END_CRYSTAL && packet.getHand() == Hand.MAIN_HAND;
                        if (mc.player.getStackInHand(packet.getHand()).getItem() == Items.END_CRYSTAL || silent) {
                           ((IDPredict)this.module).attack((Integer)((IDPredict)this.module).offset.getValue(), (Integer)((IDPredict)this.module).packets.getValue(), (Integer)((IDPredict)this.module).delay.getValue());
                           ((IDPredict)this.module).timer.reset();
                           ++((IDPredict)this.module).attacks;
                           if (((IDPredict)this.module).attackTimer.passed(1020L)) {
                              ((IDPredict)this.module).attackTimer.reset();
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }
}