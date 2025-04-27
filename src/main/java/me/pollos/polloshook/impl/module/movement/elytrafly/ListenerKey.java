package me.pollos.polloshook.impl.module.movement.elytrafly;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyPressAction;
import me.pollos.polloshook.impl.events.keyboard.KeyPressEvent;

public class ListenerKey extends ModuleListener<ElytraFly, KeyPressEvent> {
   public ListenerKey(ElytraFly module) {
      super(module, KeyPressEvent.class);
   }

   public void call(KeyPressEvent event) {
      if (((ElytraFly)this.module).isChina() && event.getAction() == KeyPressAction.PRESS && event.getKey() == 32) {
         ((ElytraFly)this.module).setChina(false);
      }

   }
}
