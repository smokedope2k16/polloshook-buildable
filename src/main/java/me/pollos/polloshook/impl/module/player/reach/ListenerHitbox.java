package me.pollos.polloshook.impl.module.player.reach;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerHitbox extends ModuleListener<Reach, Reach.HitboxEvent> {
   public ListenerHitbox(Reach module) {
      super(module, Reach.HitboxEvent.class);
   }

   public void call(Reach.HitboxEvent event) {
      if ((Float)((Reach)this.module).hitbox.getValue() != 0.0F) {
         event.setAdd((Float)((Reach)this.module).hitbox.getValue());
         event.setCanceled(true);
      }
   }
}
