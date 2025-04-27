package me.pollos.polloshook.impl.module.player.yaw;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.player.yaw.mode.YawMode;

public class YawLock extends ToggleableModule {
   protected final EnumValue<YawMode> mode;
   protected final Value<Boolean> noCameraTurn;

   public YawLock() {
      super(new String[]{"Yaw", "yawlock", "rotationslock"}, Category.PLAYER);
      this.mode = new EnumValue(YawMode.DEGREE_90, new String[]{"Lock", "l"});
      this.noCameraTurn = new Value(false, new String[]{"DisableCamera", "disable"});
      this.offerValues(new Value[]{this.mode, this.noCameraTurn});
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerTurnHead(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }
}
