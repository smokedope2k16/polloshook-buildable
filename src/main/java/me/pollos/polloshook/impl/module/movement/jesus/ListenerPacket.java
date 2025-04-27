package me.pollos.polloshook.impl.module.movement.jesus;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.asm.mixins.network.IPlayerMoveC2SPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.movement.fly.Fly;
import me.pollos.polloshook.impl.module.movement.jesus.mode.JesusMode;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ListenerPacket extends SafeModuleListener<Jesus, PacketEvent.Send<?>> {
   private float offset = 0.0F;

   public ListenerPacket(Jesus module) {
      super(module, PacketEvent.Send.class);
   }

   public void safeCall(PacketEvent.Send<?> event) {
      if (!PositionUtil.inLiquid(true)) {
         Packet var3 = event.getPacket();
         if (var3 instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)var3;
            if ((((Jesus)this.module).mode.getValue() == JesusMode.SOLID || ((Jesus)this.module).mode.getValue() == JesusMode.STRICT_SOLID) && ((Fly)Managers.getModuleManager().get(Fly.class)).isEnabled() && packet.changesPosition()) {
               if (((Jesus)this.module).mode.getValue() == JesusMode.STRICT_SOLID) {
                  this.offset += 0.12F;
                  if (this.offset > 0.4F) {
                     this.offset = 0.2F;
                  }
               } else {
                  this.offset = mc.player.age % 2 == 0 ? 0.0F : 0.05F;
               }

               IPlayerMoveC2SPacket access = (IPlayerMoveC2SPacket)packet;
               access.setY(access.getRawY() - (double)this.offset);
            }
         }

      }
   }
}
