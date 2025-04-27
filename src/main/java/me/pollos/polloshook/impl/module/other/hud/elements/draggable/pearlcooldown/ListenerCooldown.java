package me.pollos.polloshook.impl.module.other.hud.elements.draggable.pearlcooldown;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.item.SetPearlCooldownEvent;

public class ListenerCooldown extends ModuleListener<PearlCooldown, SetPearlCooldownEvent> {
   public ListenerCooldown(PearlCooldown module) {
      super(module, SetPearlCooldownEvent.class);
   }

   public void call(SetPearlCooldownEvent event) {
      if ((Boolean)((PearlCooldown)this.module).setItemCooldown.getValue()) {
         event.setTicks((int)((Float)((PearlCooldown)this.module).cooldown.getValue() * 20.0F));
         event.setCanceled(true);
      }

   }
}
