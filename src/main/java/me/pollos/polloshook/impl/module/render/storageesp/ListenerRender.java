package me.pollos.polloshook.impl.module.render.storageesp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.RenderEvent;

public class ListenerRender extends ModuleListener<StorageESP, RenderEvent> {
   public ListenerRender(StorageESP module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      ((StorageESP)this.module).onRender(event);
   }
}
