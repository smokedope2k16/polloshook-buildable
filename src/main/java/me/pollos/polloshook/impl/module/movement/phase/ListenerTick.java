package me.pollos.polloshook.impl.module.movement.phase;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.update.TickEvent;
import me.pollos.polloshook.impl.module.movement.phase.util.PhaseMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;

public class ListenerTick extends SafeModuleListener<Phase, TickEvent.Post> {
   public ListenerTick(Phase module) {
      super(module, TickEvent.Post.class);
   }

   public void safeCall(TickEvent.Post event) {
      if (((Phase)this.module).mode.getValue() == PhaseMode.CLIP && ((Phase)this.module).doBounds) {
         double yaw = Math.random() * 3.141592653589793D * 2.0D;
         double x = mc.player.getX() + Math.cos(yaw);
         double z = mc.player.getZ() + Math.sin(yaw);
         PacketUtil.send(new PositionAndOnGround(x, -87.0D, z, true));
         ((Phase)this.module).doBounds = false;
      }

   }
}
