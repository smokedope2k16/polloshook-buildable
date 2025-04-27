package me.pollos.polloshook.impl.module.misc.announcer;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.entity.AttackEntityEvent;
import me.pollos.polloshook.impl.module.misc.announcer.modes.AnnouncerAction;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerAttack extends ModuleListener<Announcer, AttackEntityEvent> {
   public ListenerAttack(Announcer module) {
      super(module, AttackEntityEvent.class);
   }

   public void call(AttackEntityEvent event) {
      if (event.getEntity() instanceof PlayerEntity) {
         if (!event.getEntity().isAlive()) {
            return;
         }

         if (!(Boolean)((Announcer)this.module).attack.getValue()) {
            return;
         }

         String name = event.getEntity().getName().getString();
         if (name.equals(mc.player.getName().getString())) {
            return;
         }

         ((Announcer)this.module).attackPlayer = name;
         ((Announcer)this.module).addEvent(AnnouncerAction.ATTACK);
      }

   }
}
