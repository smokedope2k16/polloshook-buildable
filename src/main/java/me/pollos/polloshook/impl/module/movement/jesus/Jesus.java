package me.pollos.polloshook.impl.module.movement.jesus;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.movement.PositionUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.jesus.mode.JesusMode;

public class Jesus extends ToggleableModule {
   protected final EnumValue<JesusMode> mode;
   protected int stage;

   public Jesus() {
      super(new String[]{"Jesus", "hesus", "waterwalk", "peter"}, Category.MOVEMENT);
      this.mode = new EnumValue(JesusMode.SOLID, new String[]{"Mode", "m"});
      this.stage = 0;
      this.offerValues(new Value[]{this.mode});
      this.offerListeners(new Listener[]{new ListenerCollide(this), new ListenerPacket(this), new ListenerMotion(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   public boolean isGoingCrazyAF() {
      return PositionUtil.inLiquid(true) && this.isEnabled();
   }
}
