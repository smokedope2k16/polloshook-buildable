package me.pollos.polloshook.impl.module.movement.noslow;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.asm.ducks.entity.ILivingEntity;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.update.TickEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

public class ListenerTick extends SafeModuleListener<NoSlow, TickEvent> {
   public ListenerTick(NoSlow module) {
      super(module, TickEvent.class);
   }

   public void safeCall(TickEvent event) {
      if ((Boolean)((NoSlow)this.module).noJumpDelay.getValue()) {
         ILivingEntity ent = (ILivingEntity)mc.player;
         ent.setLastJumpCooldown(0);
      }

      if ((Boolean)((NoSlow)this.module).grim.getValue() && mc.player.isUsingItem()) {
         Hand hand = mc.player.getActiveHand();
         if (hand == Hand.MAIN_HAND) {
            ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
               return new PlayerInteractItemC2SPacket(Hand.OFF_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
            });
         } else if (hand == Hand.OFF_HAND) {
            PacketUtil.send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 8 + 1));
            if ((Boolean)((NoSlow)this.module).extra.getValue()) {
               PacketUtil.send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot % 7 + 2));
            }

            PacketUtil.send(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
         }
      }

   }
}
