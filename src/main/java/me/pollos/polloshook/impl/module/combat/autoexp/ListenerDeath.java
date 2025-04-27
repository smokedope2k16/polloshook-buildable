package me.pollos.polloshook.impl.module.combat.autoexp;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import net.minecraft.client.gui.screen.DeathScreen;

public class ListenerDeath extends ModuleListener<AutoExp, ScreenEvent> {
   public ListenerDeath(AutoExp module) {
      super(module, ScreenEvent.class);
   }

   public void call(ScreenEvent event) {
      if (event.getScreen() instanceof DeathScreen) {
         ((AutoExp)this.module).setEnabled(false);
      }

   }
}
