package me.pollos.polloshook.impl.module.combat.autoexp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import me.pollos.polloshook.impl.events.entity.UseItemEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class ListenerUseItem extends ModuleListener<AutoExp, UseItemEvent> {
   public ListenerUseItem(AutoExp module) {
      super(module, UseItemEvent.class);
   }

   public void call(UseItemEvent event) {
      if ((Boolean)((AutoExp)this.module).strict.getValue() && ((AutoExp)this.module).sending) {
         event.setCanceled(true);
         ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
            return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
         });
         mc.player.swingHand(Hand.MAIN_HAND);
      }

   }
}
