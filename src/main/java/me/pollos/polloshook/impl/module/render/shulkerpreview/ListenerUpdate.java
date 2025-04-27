package me.pollos.polloshook.impl.module.render.shulkerpreview;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.update.UpdateEvent;

public class ListenerUpdate extends ModuleListener<ShulkerPreview, UpdateEvent> {
   public ListenerUpdate(ShulkerPreview module) {
      super(module, UpdateEvent.class);
   }

   public void call(UpdateEvent event) {
      if (mc.currentScreen == null) {
         ((ShulkerPreview)this.module).searchTarget = "";
         ((ShulkerPreview)this.module).searching = false;
         ((ShulkerPreview)this.module).paused = false;
      }

      if (!((ShulkerPreview)this.module).searching) {
         ((ShulkerPreview)this.module).paused = false;
      }

   }
}
