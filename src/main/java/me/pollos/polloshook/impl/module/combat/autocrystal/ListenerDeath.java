package me.pollos.polloshook.impl.module.combat.autocrystal;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import net.minecraft.client.gui.screen.DeathScreen;

public class ListenerDeath extends ModuleListener<AutoCrystal, ScreenEvent> {
   public ListenerDeath(AutoCrystal module) {
      super(module, ScreenEvent.class);
   }

   public void call(ScreenEvent event) {
      if (event.getScreen() instanceof DeathScreen) {
         ((AutoCrystal)this.module).reset();
      }

   }
}
