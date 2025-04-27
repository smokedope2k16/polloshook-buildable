package me.pollos.polloshook.impl.module.movement.longjump;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.longjump.type.mode.LongJumpMode;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ListenerPosLook extends ModuleListener<LongJump, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(LongJump module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      ((LongJumpMode)((LongJump)this.module).mode.getValue()).getType().reset();
      if ((Boolean)((LongJump)this.module).autoDisable.getValue()) {
         ((LongJump)this.module).setEnabled(false);
      }

   }
}
