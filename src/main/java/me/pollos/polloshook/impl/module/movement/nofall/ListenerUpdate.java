package me.pollos.polloshook.impl.module.movement.nofall;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.update.UpdateEvent;
import me.pollos.polloshook.impl.module.movement.nofall.mode.NoFallMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;

public class ListenerUpdate extends ModuleListener<NoFall, UpdateEvent> {
   public ListenerUpdate(NoFall module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (!PlayerUtil.isCreative() && !mc.player.isFallFlying()) {
         if (((NoFall)this.module).mode.getValue() == NoFallMode.GRIM) {
            PacketUtil.send(new Full(mc.player.getX(), mc.player.getY() + 1.0E-9D, mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), true));
            mc.player.onLanding();
         }

      }
   }
}
