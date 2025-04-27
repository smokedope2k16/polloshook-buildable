package me.pollos.polloshook.impl.manager.minecraft.combat;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.impl.events.entity.EntityWorldEvent;
import me.pollos.polloshook.impl.events.entity.PearlThrowEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

public class PearlManager extends SubscriberImpl {
   public PearlManager() {
      this.listeners.add(new Listener<EntityWorldEvent.Add>(EntityWorldEvent.Add.class) {
         public void call(EntityWorldEvent.Add event) {
            if (mc.player != null && mc.world != null) {
               Entity var3 = event.getEntity();
               if (var3 instanceof EnderPearlEntity) {
                  EnderPearlEntity pearl = (EnderPearlEntity)var3;
                  Entity var4 = pearl.getOwner();
                  if (var4 instanceof PlayerEntity) {
                     PlayerEntity player = (PlayerEntity)var4;
                     PollosHook.getEventBus().dispatch(new PearlThrowEvent(player, pearl));
                  }
               }

            }
         }
      });
   }
}
