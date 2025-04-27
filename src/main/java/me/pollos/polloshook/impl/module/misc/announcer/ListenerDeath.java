package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.impl.events.entity.DeathEvent;
import me.pollos.polloshook.impl.module.combat.aura.Aura;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerDeath extends ModuleListener<Announcer, DeathEvent> {
   public ListenerDeath(Announcer module) {
      super(module, DeathEvent.class);
   }

   public void call(DeathEvent event) {
      boolean var10000;
      LivingEntity entity;
      label31: {
         entity = event.getEntity();
         if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            if (entity != mc.player && !Managers.getFriendManager().isFriend(player)) {
               var10000 = true;
               break label31;
            }
         }

         var10000 = false;
      }

      boolean isValid = var10000;
      boolean isTarget = ((AutoCrystal)Managers.getModuleManager().get(AutoCrystal.class)).getEnemy() == entity || ((Aura)Managers.getModuleManager().get(Aura.class)).getTarget() == entity;
      if ((Boolean)((Announcer)this.module).kills.getValue() && isValid && isTarget) {
         ((Announcer)this.module).killPlayer = event.getEntity().getName().getString();
         String msg = ((Announcer)this.module).getMessage(AnnouncerAction.KILL, 0.0F);
         NetworkUtil.sendInChat(msg);
      }

   }
}
