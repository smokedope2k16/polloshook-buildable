package me.pollos.polloshook.impl.module.render.norender;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderEntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.TntEntity;

public class ListenerRenderEntity extends ModuleListener<NoRender, RenderEntityEvent> {
   public ListenerRenderEntity(NoRender module) {
      super(module, RenderEntityEvent.class);
   }

   public void call(RenderEntityEvent event) {
      Entity entity = event.getEntity();
      if ((Boolean)((NoRender)this.module).getSand().getValue() && entity instanceof FallingBlockEntity) {
         event.setCanceled(true);
      } else if ((Boolean)((NoRender)this.module).getDynamite().getValue() && entity instanceof TntEntity) {
         event.setCanceled(true);
      } else {
         if ((Boolean)((NoRender)this.module).getEntities().getValue() && ((NoRender)this.module).getNoRenderList().contains(entity)) {
            event.setCanceled(true);
         }

      }
   }
}
