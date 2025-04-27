package me.pollos.polloshook.impl.module.player.norotate;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.mixins.network.IPlayerMoveC2SPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

public class ListenerPacket extends ModuleListener<NoRotate, PacketEvent.Send<Full>> {
   public ListenerPacket(NoRotate module) {
      super(module, PacketEvent.Send.class, Full.class);
   }

   public void call(PacketEvent.Send<Full> event) {
      if (((NoRotate)this.module).rotations != null) {
         IPlayerMoveC2SPacket access = (IPlayerMoveC2SPacket)event.getPacket();
         access.setYaw(((NoRotate)this.module).rotations[0]);
         access.setPitch(((NoRotate)this.module).rotations[1]);
         ((NoRotate)this.module).rotations = null;
      }
   }
}
