package me.pollos.polloshook.impl.module.misc.autorespawn;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class AutoRespawn extends ToggleableModule {
   public AutoRespawn() {
      super(new String[]{"AutoRespawn", "respawntime", "respawn", "revive"}, Category.MISC);
      this.offerListeners(new Listener[]{new ListenerDeathScreen(this)});
   }
}
