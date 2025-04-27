package me.pollos.polloshook.impl.module.misc.antiinteract;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class ListenerInteractBlock extends ModuleListener<AntiInteract, PacketEvent.Send<PlayerInteractBlockC2SPacket>> {
   private boolean interact;

   public ListenerInteractBlock(AntiInteract module) {
      super(module, PacketEvent.Send.class, PlayerInteractBlockC2SPacket.class);
   }

   public void call(PacketEvent.Send<PlayerInteractBlockC2SPacket> event) {
      if (!mc.player.isSneaking() && !Managers.getPositionManager().isSneaking()) {
         if ((Boolean)((AntiInteract)this.module).packets.getValue()) {
            PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket)event.getPacket();
            BlockHitResult result = packet.getBlockHitResult();
            BlockPos pos = result.getBlockPos();
            Hand hand = packet.getHand();
            if (hand == Hand.OFF_HAND & this.interact) {
               this.interact = false;
               event.setCanceled(true);
               return;
            }

            if (((AntiInteract)this.module).isValid(pos, hand)) {
               event.setCanceled(true);
               this.interact = true;
            }
         }

      }
   }
}
