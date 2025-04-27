package me.pollos.polloshook.impl.module.other.hud.elements.draggable.clientmessages;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.EntityUtil;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.events.entity.DeathEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerDeath extends ModuleListener<ClientMessages, DeathEvent> {
   public ListenerDeath(ClientMessages module) {
      super(module, DeathEvent.class);
   }

   public void call(DeathEvent event) {
      LivingEntity player = event.getEntity();
      if (player instanceof PlayerEntity) {
         String name = EntityUtil.getName(player);
         if (Managers.getPopManager().getPopMap().containsKey(name)) {
            int pops = (Integer)Managers.getPopManager().getPopMap().get(name);
            ((ClientMessages)this.module).displayMessage("%s died after popping their %s totem".formatted(new Object[]{name, pops + TextUtil.toOrdinal(pops)}));
         }
      }

   }
}
