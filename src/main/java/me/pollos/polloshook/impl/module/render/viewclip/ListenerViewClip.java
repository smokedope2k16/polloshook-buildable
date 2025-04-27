package me.pollos.polloshook.impl.module.render.viewclip;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerViewClip extends ModuleListener<ViewClip, ViewClip.ViewClipEvent> {
   public ListenerViewClip(ViewClip module) {
      super(module, ViewClip.ViewClipEvent.class);
   }

   public void call(ViewClip.ViewClipEvent event) {
      event.setAdd((Float)((ViewClip)this.module).factor.getValue());
      event.setCanceled(true);
   }
}
