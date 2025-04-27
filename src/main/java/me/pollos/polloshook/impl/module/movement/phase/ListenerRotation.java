package me.pollos.polloshook.impl.module.movement.phase;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.phase.util.PhaseMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;

public class ListenerRotation extends ModuleListener<Phase, PacketEvent.Send<LookAndOnGround>> {
   public ListenerRotation(Phase module) {
      super(module, PacketEvent.Send.class, LookAndOnGround.class);
   }

   public void call(PacketEvent.Send<LookAndOnGround> event) {
      if (((Phase)this.module).mode.getValue() == PhaseMode.CLIP) {
         event.setCanceled(true);
      }

   }
}
