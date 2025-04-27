package me.pollos.polloshook.impl.module.movement.tridentfly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.ducks.world.IClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

public class ListenerUse extends ModuleListener<TridentFly, TridentFly.UseTridentEvent> {
   public ListenerUse(TridentFly module) {
      super(module, TridentFly.UseTridentEvent.class);
   }

   public void call(TridentFly.UseTridentEvent event) {
      if ((Boolean)((TridentFly)this.module).grim.getValue()) {
         ((IClientPlayerInteractionManager)mc.interactionManager).sendPacketWithSequence(mc.world, (sequence) -> {
            return new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, sequence, mc.player.getYaw(), mc.player.getPitch());
         });
      }

      if (!(Boolean)((TridentFly)this.module).requireMouseDown.getValue() && ((TridentFly)this.module).flag) {
         mc.options.useKey.setPressed(((TridentFly)this.module).flag = false);
      }

      ((TridentFly)this.module).timer.reset();
   }
}
