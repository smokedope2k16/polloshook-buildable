package me.pollos.polloshook.impl.module.render.esp;

import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.EntityInterpolationEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

public class ListenerInterp extends ModuleListener<ESP, EntityInterpolationEvent> {
   public ListenerInterp(ESP module) {
      super(module, EntityInterpolationEvent.class);
   }

   public void call(EntityInterpolationEvent event) {
      LivingEntity var3 = event.getEntity();
      if (var3 instanceof PlayerEntity) {
         PlayerEntity player = (PlayerEntity)var3;
         Vec3d vec = new Vec3d(event.getX(), event.getY(), event.getZ());
         Iterator<Entry<UUID, Vec3d>> var4 = ((ESP)this.module).ignoredPlayers.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<UUID, Vec3d> entry = var4.next();
            if (entry.getKey() == player.getUuid() && !entry.getValue().equals(vec)) {
               ((ESP)this.module).ignoredPlayers.remove(entry.getKey());
            }
         }

         if (player.getMainHandStack().getItem() != Items.CROSSBOW) {
            return;
         }

         UUID uuid = player.getUuid();
         if (((ESP)this.module).ignoredPlayers.containsKey(uuid)) {
            ((ESP)this.module).ignoredPlayers.replace(uuid, vec);
         } else {
            ((ESP)this.module).ignoredPlayers.put(uuid, vec);
         }
      }
   }
}
