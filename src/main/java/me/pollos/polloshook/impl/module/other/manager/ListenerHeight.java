package me.pollos.polloshook.impl.module.other.manager;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerHeight extends ModuleListener<Manager, Manager.AspectRatioHeightEvent> {
   public ListenerHeight(Manager module) {
      super(module, Manager.AspectRatioHeightEvent.class);
   }

   public void call(Manager.AspectRatioHeightEvent event) {
      if ((Boolean)((Manager)this.module).aspectRatioChanger.getValue()) {
         event.setHeight((float)(Integer)((Manager)this.module).height.getValue());
      }

   }
}
