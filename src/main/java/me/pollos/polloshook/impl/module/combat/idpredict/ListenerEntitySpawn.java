package me.pollos.polloshook.impl.module.combat.idpredict;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

public class ListenerEntitySpawn extends ModuleListener<IDPredict, PacketEvent.Receive<EntitySpawnS2CPacket>> {
   public ListenerEntitySpawn(IDPredict module) {
      super(module, PacketEvent.Receive.class, EntitySpawnS2CPacket.class);
   }

   public void call(PacketEvent.Receive<EntitySpawnS2CPacket> event) {
      ((IDPredict)this.module).checkID(((EntitySpawnS2CPacket)event.getPacket()).getEntityId());
   }
}
