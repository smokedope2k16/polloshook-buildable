package me.pollos.polloshook.impl.module.movement.nofall;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.asm.mixins.network.IPlayerMoveC2SPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.nofall.mode.NoFallMode;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ListenerPacket extends SafeModuleListener<NoFall, PacketEvent.Send<?>> {
   public ListenerPacket(NoFall module) {
      super(module, PacketEvent.Send.class);
   }

   public void safeCall(PacketEvent.Send<?> event) {
      if (mc.player.fallDistance > (Float)((NoFall)this.module).fallDistance.getValue()) {
         Packet<?> packet = event.getPacket();
         if (packet instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket moveC2SPacket = (PlayerMoveC2SPacket)packet;
            IPlayerMoveC2SPacket access = (IPlayerMoveC2SPacket)packet;
            if (moveC2SPacket.isOnGround() || PlayerUtil.isCreative() || mc.player.isFallFlying()) {
               return;
            }

            switch((NoFallMode)((NoFall)this.module).mode.getValue()) {
            case ANTI:
               access.setY(access.getRawY() + 0.10000000149011612D);
               break;
            case PACKET:
               access.setOnGround(true);
               break;
            case FLY:
               boolean oldFly = mc.player.getAbilities().allowFlying;
               mc.player.setOnGround(true);
               mc.player.getAbilities().flying = true;
               mc.player.getAbilities().allowFlying = true;
               access.setOnGround(false);
               mc.player.velocityModified = true;
               mc.player.getAbilities().flying = false;
               mc.player.getAbilities().allowFlying = oldFly;
               mc.player.jump();
            }
         }

      }
   }
}
