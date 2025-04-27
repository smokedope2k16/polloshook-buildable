package me.pollos.polloshook.impl.module.player.fastplace;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.entity.UseItemEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult.Type;

public class ListenerUseItem extends ModuleListener<FastPlace, UseItemEvent> {
   private boolean weirdFix;

   public ListenerUseItem(FastPlace module) {
      super(module, UseItemEvent.class);
   }

   public void call(UseItemEvent event) {
      PlayerEntity player = mc.player;
      if (player != null) {
         Hand hand = event.getHand();
         ItemStack stack = mc.player.getStackInHand(hand);
         if ((Boolean)((FastPlace)this.module).safePearl.getValue() && mc.crosshairTarget != null && mc.crosshairTarget.getType() != null) {
            boolean isEntityType = (Boolean)((FastPlace)this.module).entities.getValue() && mc.crosshairTarget.getType() == Type.ENTITY;
            if (stack.getItem() instanceof EnderPearlItem && (isEntityType || mc.crosshairTarget.getType() == Type.BLOCK)) {
               event.setCanceled(true);
            }
         }

         if (hand == Hand.OFF_HAND && this.weirdFix) {
            event.setCanceled(true);
            this.weirdFix = false;
         } else {
            if ((Boolean)((FastPlace)this.module).ghostFix.getValue() && stack.getItem() == Items.END_CRYSTAL) {
               event.setCanceled(true);
               if ((Boolean)((FastPlace)this.module).rotate.getValue()) {
                  PacketUtil.send(new Full(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround()));
               }

               ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
                  return new PlayerInteractItemC2SPacket(hand, sequence, mc.player.getYaw(), mc.player.getPitch());
               });
               mc.player.swingHand(hand);
               this.weirdFix = true;
            }

         }
      }
   }
}
