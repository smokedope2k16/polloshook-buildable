package me.pollos.polloshook.impl.module.render.shulkerpreview;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.text.TextUtil;
import me.pollos.polloshook.impl.gui.click.component.values.StringComponent;

public class ListenerKey extends ModuleListener<ShulkerPreview, ShulkerPreview.TypeStringEvent> {
   public ListenerKey(ShulkerPreview module) {
      super(module, ShulkerPreview.TypeStringEvent.class);
   }

   public void call(ShulkerPreview.TypeStringEvent event) {
      if ((Boolean)((ShulkerPreview)this.module).search.getValue() && ((ShulkerPreview)this.module).searching) {
         int kc = event.getKeyCode();
         if (kc == 256) {
            ((ShulkerPreview)this.module).searchTarget = "";
         } else if (kc == 259 && !TextUtil.isNullOrEmpty(((ShulkerPreview)this.module).searchTarget)) {
            ((ShulkerPreview)this.module).searchTarget = StringComponent.removeLastChar(((ShulkerPreview)this.module).searchTarget);
            event.setCanceled(true);
         } else {
            ShulkerPreview var10000;
            if (kc == 32) {
               var10000 = (ShulkerPreview)this.module;
               var10000.searchTarget = var10000.searchTarget + " ";
               event.setCanceled(true);
            } else if (kc == 257) {
               ((ShulkerPreview)this.module).paused = true;
            } else if (event.getString() != null && !((ShulkerPreview)this.module).paused) {
               var10000 = (ShulkerPreview)this.module;
               String var10001 = var10000.searchTarget;
               var10000.searchTarget = var10001 + event.getString();
               event.setCanceled(true);
            }
         }
      }

   }
}
