package me.pollos.polloshook.impl.module.player.suicide;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import net.minecraft.client.gui.screen.DeathScreen;

public class ListenerDeath extends ModuleListener<Suicide, ScreenEvent> {
   public ListenerDeath(Suicide module) {
      super(module, ScreenEvent.class);
   }

   public void call(ScreenEvent event) {
      if (event.getScreen() instanceof DeathScreen) {
         ((Suicide)this.module).setEnabled(false);
      }

   }
}
