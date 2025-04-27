package me.pollos.polloshook.impl.module.movement.boatfly;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.api.minecraft.network.PacketUtil;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.util.Hand;

public class ListenerEntityPassengers extends SafeModuleListener<BoatFly, PacketEvent.Receive<EntityPassengersSetS2CPacket>> {
   public ListenerEntityPassengers(BoatFly module) {
      super(module, PacketEvent.Receive.class, EntityPassengersSetS2CPacket.class);
   }

   public void safeCall(PacketEvent.Receive<EntityPassengersSetS2CPacket> event) {
      if ((Boolean)((BoatFly)this.module).remount.getValue()) {
         EntityPassengersSetS2CPacket packet = (EntityPassengersSetS2CPacket)event.getPacket();
         int id = packet.getEntityId();
         if (mc.player.getVehicle() != null && id == mc.player.getId()) {
            event.setCanceled(true);
            int[] var4 = packet.getPassengerIds();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               int passId = var4[var6];
               if (id == passId) {
                  for(int i = 0; i < 3; ++i) {
                     PacketUtil.send(PlayerInteractEntityC2SPacket.interact(mc.player.getVehicle(), mc.player.isSneaking(), Hand.MAIN_HAND));
                  }
               }
            }
         }

      }
   }
}
