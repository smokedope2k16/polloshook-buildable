package me.pollos.polloshook.impl.module.combat.autocrystal;

import it.unimi.dsi.fastutil.ints.IntListIterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;

public class ListenerDestroyEntity extends ModuleListener<AutoCrystal, PacketEvent.Receive<EntitiesDestroyS2CPacket>> {
   public ListenerDestroyEntity(AutoCrystal module) {
      super(module, PacketEvent.Receive.class, EntitiesDestroyS2CPacket.class);
   }

   public void call(PacketEvent.Receive<EntitiesDestroyS2CPacket> event) {
      if (mc.world != null) {
         EntitiesDestroyS2CPacket packet = (EntitiesDestroyS2CPacket)event.getPacket();
         IntListIterator var3 = packet.getEntityIds().iterator();

         while(var3.hasNext()) {
            int id = (Integer)var3.next();
            Entity entity = mc.world.getEntityById(id);
            if (entity != null && entity instanceof EndCrystalEntity) {
               if (id == ((AutoCrystal)this.module).getLastSpawnID() && ((AutoCrystal)this.module).getCrystalDelays().containsKey(id) && mc.player.squaredDistanceTo(entity) < 128.0D) {
                  ((AutoCrystal)this.module).calcLastAttack((Long)((AutoCrystal)this.module).getCrystalDelays().get(id));
                  ((AutoCrystal)this.module).setLastSpawnID(-9999);
               }

               ((AutoCrystal)this.module).removeAllPending(entity.getBlockPos());
               ((AutoCrystal)this.module).getConfirmedPlacePositions().remove(entity.getBlockPos());
               ((AutoCrystal)this.module).getAttacks().remove(entity.getBlockPos());
            }
         }

      }
   }
}
