package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerWidth extends ModuleListener<Manager, Manager.AspectRatioWidthEvent> {
   public ListenerWidth(Manager module) {
      super(module, Manager.AspectRatioWidthEvent.class);
   }

   public void call(Manager.AspectRatioWidthEvent event) {
      if ((Boolean)((Manager)this.module).aspectRatioChanger.getValue()) {
         event.setWidth((float)(Integer)((Manager)this.module).width.getValue());
      }

   }
}
