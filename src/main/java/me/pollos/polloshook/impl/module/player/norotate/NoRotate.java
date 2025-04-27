package me.pollos.polloshook.impl.module.player.norotate;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;

public class NoRotate extends ToggleableModule {
   protected float[] rotations = null;

   public NoRotate() {
      super(new String[]{"NoRotate", "norotations"}, Category.PLAYER);
      this.offerListeners(new Listener[]{new ListenerPosLook(this), new ListenerPacket(this)});
   }
}
