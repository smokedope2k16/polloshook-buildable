package me.pollos.polloshook.impl.module.misc.antiaim;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.misc.antiaim.mode.AntiAimPitch;
import me.pollos.polloshook.impl.module.misc.antiaim.mode.AntiAimYaw;

public class AntiAim extends ToggleableModule {
   protected final EnumValue<AntiAimYaw> yaw;
   protected final EnumValue<AntiAimPitch> pitch;
   protected final NumberValue<Float> customYaw;
   protected final NumberValue<Float> customPitch;
   protected final NumberValue<Integer> spinSpeed;
   protected final Value<Boolean> illegalAngles;

   public AntiAim() {
      super(new String[]{"AntiAim", "derp", "retard"}, Category.MISC);
      this.yaw = new EnumValue(AntiAimYaw.OFF, new String[]{"Yaw", "y"});
      this.pitch = new EnumValue(AntiAimPitch.OFF, new String[]{"Pitch", "p"});
      this.customYaw = (new NumberValue(0.0F, -180.0F, 180.0F, 0.5F, new String[]{"CustomYaw", "cyaw"})).setParent(this.yaw, AntiAimYaw.CUSTOM);
      this.customPitch = (new NumberValue(0.0F, -180.0F, 180.0F, 0.5F, new String[]{"CustomPitch", "cpitch"})).setParent(this.pitch, AntiAimPitch.CUSTOM);
      this.spinSpeed = (new NumberValue(5, 1, 20, new String[]{"SpinSpeed", "spinsped", "spin"})).setParent(this.yaw, AntiAimYaw.SPIN);
      this.illegalAngles = new Value(false, new String[]{"IllegalAngles", "illegal"});
      this.offerValues(new Value[]{this.yaw, this.customYaw, this.pitch, this.customPitch, this.spinSpeed, this.illegalAngles});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
   }
}
