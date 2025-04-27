package me.pollos.polloshook.impl.module.render.modelchanger;

import me.pollos.polloshook.api.event.listener.ModuleListener;

public class ListenerAlpha extends ModuleListener<ModelChanger, ModelChanger.ItemAlphaEvent> {
   public ListenerAlpha(ModelChanger module) {
      super(module, ModelChanger.ItemAlphaEvent.class);
   }

   public void call(ModelChanger.ItemAlphaEvent event) {
      if ((Boolean)((ModelChanger)this.module).modifyAlpha.getValue()) {
         event.setAlpha((Integer)((ModelChanger)this.module).alpha.getValue());
         event.setCanceled(true);
      }

   }
}
