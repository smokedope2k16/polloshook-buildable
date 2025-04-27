package me.pollos.polloshook.impl.module.player.nointerp;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class NoInterpolation extends ToggleableModule {
   public NoInterpolation() {
      super(new String[]{"NoInterpolation", "nointerp"}, Category.PLAYER);
      this.offerListeners(new Listener[]{new ListenerInterp(this)});
   }
}
