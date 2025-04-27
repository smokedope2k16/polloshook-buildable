package me.pollos.polloshook.impl.module.player.choruscontrol;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class ChorusControl extends ToggleableModule {
   protected PlayerPositionLookS2CPacket packet;
   protected boolean cancel;
   protected int ticksSinceCancel = 0;

   public ChorusControl() {
      super(new String[]{"ChorusControl", "chorusmanipulate"}, Category.PLAYER);
      this.offerListeners(new Listener[]{new ListenerKeyPress(this), new ListenerMove(this), new ListenerPosLook(this), new ListenerUpdate(this), new ListenerRender(this)});
   }

   protected String getTag() {
      return this.cancel ? String.valueOf(this.ticksSinceCancel) : null;
   }

   protected void onDisable() {
      this.sendPacket();
   }

   protected void sendPacket() {
      if (mc.getNetworkHandler() != null && this.packet != null) {
         mc.getNetworkHandler().onPlayerPositionLook(this.packet);
      }

      this.packet = null;
      this.cancel = false;
      this.ticksSinceCancel = 0;
   }
}
