package me.pollos.polloshook.impl.module.movement.velocity;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class ListenerFishingHook extends ModuleListener<Velocity, PacketEvent.Receive<EntityStatusS2CPacket>> {
   public ListenerFishingHook(Velocity module) {
      super(module, PacketEvent.Receive.class, EntityStatusS2CPacket.class);
   }

   public void call(PacketEvent.Receive<EntityStatusS2CPacket> event) {
      EntityStatusS2CPacket packet = (EntityStatusS2CPacket)event.getPacket();
      if (packet.getStatus() == 31 && (Boolean)((Velocity)this.module).fishingRod.getValue() && packet.getEntity(mc.world) == mc.player) {
         event.setCanceled(true);
      }

   }
}
