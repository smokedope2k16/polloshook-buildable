package me.pollos.polloshook.impl.module.render.extratab;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerRenderElement extends ModuleListener<ExtraTab, ExtraTab.RenderTabElementEvent> {
   public ListenerRenderElement(ExtraTab module) {
      super(module, ExtraTab.RenderTabElementEvent.class);
   }

   public void call(ExtraTab.RenderTabElementEvent event) {
      switch(event.getElement()) {
      case BACKGROUND:
         if ((Boolean)((ExtraTab)this.module).clear.getValue()) {
            event.setCanceled(true);
         }
         break;
      case FOOTER:
         if ((Boolean)((ExtraTab)this.module).noFooter.getValue()) {
            event.setCanceled(true);
         }
         break;
      case HEADER:
         if ((Boolean)((ExtraTab)this.module).noHeader.getValue()) {
            event.setCanceled(true);
         }
      }

   }
}
