package me.pollos.polloshook.impl.module.render.extratab;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.render.tablist.TabLimitEvent;

public class ListenerLimit extends ModuleListener<ExtraTab, TabLimitEvent> {
   public ListenerLimit(ExtraTab module) {
      super(module, TabLimitEvent.class);
   }

   public void call(TabLimitEvent event) {
      event.setCanceled(true);
   }
}
