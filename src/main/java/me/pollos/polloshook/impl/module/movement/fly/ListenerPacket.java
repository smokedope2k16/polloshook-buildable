package me.pollos.polloshook.impl.module.movement.fly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.asm.mixins.network.IPlayerMoveC2SPacket;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class ListenerPacket extends ModuleListener<Fly, PacketEvent.Send<?>> {
   public ListenerPacket(Fly module) {
      super(module, PacketEvent.Send.class);
   }

   public void call(PacketEvent.Send<?> event) {
      if ((Boolean)((Fly)this.module).spoofGround.getValue()) {
         Packet var3 = event.getPacket();
         if (var3 instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket)var3;
            ((IPlayerMoveC2SPacket)packet).setOnGround(true);
         }
      }

   }
}
