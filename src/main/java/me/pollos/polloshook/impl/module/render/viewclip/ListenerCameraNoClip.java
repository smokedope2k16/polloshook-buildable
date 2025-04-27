package me.pollos.polloshook.impl.module.render.viewclip;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerCameraNoClip extends ModuleListener<ViewClip, ViewClip.CameraNoClipEvent> {
   public ListenerCameraNoClip(ViewClip module) {
      super(module, ViewClip.CameraNoClipEvent.class);
   }

   public void call(ViewClip.CameraNoClipEvent event) {
      if ((Boolean)((ViewClip)this.module).noClip.getValue()) {
         event.setCanceled(true);
      }

   }
}
