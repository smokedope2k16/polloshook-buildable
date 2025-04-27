package me.pollos.polloshook.impl.module.movement.phase;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.manager.minecraft.movement.PositionManager;
import me.pollos.polloshook.impl.module.movement.phase.util.PhaseMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class ListenerPositionRotation extends ModuleListener<Phase, PacketEvent.Send<Full>> {
   public ListenerPositionRotation(Phase module) {
      super(module, PacketEvent.Send.class, Full.class);
   }

   public void call(PacketEvent.Send<Full> event) {
      if (((Phase)this.module).mode.getValue() == PhaseMode.CLIP) {
         PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)event.getPacket();
         PositionManager positionManager = Managers.getPositionManager();
         double serverX = positionManager.getX();
         double serverY = positionManager.getY();
         double serverZ = positionManager.getZ();
         double x = packet.getX(serverX);
         double y = packet.getY(serverY);
         double z = packet.getZ(serverZ);
         event.setCanceled(true);
         if (serverX != x || serverY != y || serverZ != z) {
            PacketUtil.send(new PositionAndOnGround(x, y, z, packet.isOnGround()));
         }
      }

   }
}
