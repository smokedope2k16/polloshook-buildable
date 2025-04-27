package me.pollos.polloshook.impl.module.render.nametags;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderNametagEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class ListenerNametag extends ModuleListener<Nametags, RenderNametagEvent> {
   public ListenerNametag(Nametags module) {
      super(module, RenderNametagEvent.class);
   }

   public void call(RenderNametagEvent event) {
      Entity e = event.getEntity();
      if (e instanceof PlayerEntity) {
         event.setCanceled(true);
      }

   }
}
