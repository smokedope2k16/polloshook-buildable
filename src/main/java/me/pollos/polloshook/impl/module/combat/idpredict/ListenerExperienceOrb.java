package me.pollos.polloshook.impl.module.combat.idpredict;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;

public class ListenerExperienceOrb extends ModuleListener<IDPredict, PacketEvent.Receive<ExperienceOrbSpawnS2CPacket>> {
   public ListenerExperienceOrb(IDPredict module) {
      super(module, PacketEvent.Receive.class, ExperienceOrbSpawnS2CPacket.class);
   }

   public void call(PacketEvent.Receive<ExperienceOrbSpawnS2CPacket> event) {
      ((IDPredict)this.module).checkID(((ExperienceOrbSpawnS2CPacket)event.getPacket()).getEntityId());
   }
}
