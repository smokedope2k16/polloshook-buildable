package me.pollos.polloshook.impl.module.render.esp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class ListenerPosLook extends ModuleListener<ESP, PacketEvent.Receive<PlayerPositionLookS2CPacket>> {
   public ListenerPosLook(ESP module) {
      super(module, PacketEvent.Receive.class, 10000, PlayerPositionLookS2CPacket.class);
   }

   public void call(PacketEvent.Receive<PlayerPositionLookS2CPacket> event) {
      if (mc.player != null) {
         PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)event.getPacket();
         ((ESP)this.module).ignoreVec = new Vec3d[]{mc.player.getPos(), new Vec3d(packet.getX(), packet.getY(), packet.getZ())};
      }
   }
}
