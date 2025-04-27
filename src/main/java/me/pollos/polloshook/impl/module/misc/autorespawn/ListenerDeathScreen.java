package me.pollos.polloshook.impl.module.misc.autorespawn;

import me.pollos.polloshook.api.event.listener.SafeModuleListener;
import me.pollos.polloshook.impl.events.gui.ScreenEvent;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;

public class ListenerDeathScreen extends SafeModuleListener<AutoRespawn, ScreenEvent> {
   public ListenerDeathScreen(AutoRespawn module) {
      super(module, ScreenEvent.class);
   }

   public void safeCall(ScreenEvent event) {
      Screen s = event.getScreen();
      if (s instanceof DeathScreen) {
         mc.currentScreen = null;
         mc.player.requestRespawn();
      }

   }
}
