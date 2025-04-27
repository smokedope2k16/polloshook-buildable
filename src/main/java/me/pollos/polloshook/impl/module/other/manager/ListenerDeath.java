package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.math.RandomUtil;
import me.pollos.polloshook.impl.events.entity.DeathEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ListenerDeath extends ModuleListener<Manager, DeathEvent> {
   public ListenerDeath(Manager module) {
      super(module, DeathEvent.class);
   }

   public void call(DeathEvent event) {
      if (mc.world != null) {
         LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
         Entity entity = event.getEntity();
         if ((Boolean)((Manager)this.module).smite.getValue() && mc.world.getEntityById(lightning.getId()) == null) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
               lightning.copyPositionAndRotation(entity);
               mc.world.playSound(lightning.getX(), lightning.getY(), lightning.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 10000.0F, 0.8F + RandomUtil.getRandom().nextFloat() * 0.2F, false);
               mc.world.playSound(lightning.getX(), lightning.getY(), lightning.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 2.0F, 0.5F + RandomUtil.getRandom().nextFloat() * 0.2F, false);
               mc.world.addEntity(lightning);
            }

         }
      }
   }
}