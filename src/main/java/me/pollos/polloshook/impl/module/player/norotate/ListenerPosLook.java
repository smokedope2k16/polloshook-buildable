package me.pollos.polloshook.impl.module.player.norotate;

import java.util.Set;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.mixins.network.IPlayerPositionLookS2CPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;

public class ListenerPosLook extends ModuleListener<NoRotate, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(NoRotate module) {
      super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      if (mc.player != null && mc.world != null) {
         PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)event.getPacket();
         ((NoRotate)this.module).rotations = new float[]{packet.getYaw(), packet.getPitch()};
         Set<PositionFlag> flags = ((PlayerPositionLookS2CPacket)event.getPacket()).getFlags();
         if (flags.remove(PositionFlag.X_ROT)) {
            ((NoRotate)this.module).rotations[0] = packet.getYaw() + mc.player.getYaw();
         }

         if (flags.remove(PositionFlag.Y_ROT)) {
            ((NoRotate)this.module).rotations[1] = packet.getPitch() + mc.player.getPitch();
         }

         IPlayerPositionLookS2CPacket access = (IPlayerPositionLookS2CPacket)packet;
         access.setPitch(mc.player.getPitch());
         access.setYaw(mc.player.getYaw());
      }
   }
}
