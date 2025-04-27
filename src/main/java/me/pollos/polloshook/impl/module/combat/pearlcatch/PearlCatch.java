package me.pollos.polloshook.impl.module.combat.pearlcatch;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.combat.autocrystal.mode.AutoSwitch;

public class PearlCatch extends ToggleableModule {
   protected final NumberValue<Float> range = (new NumberValue(6.0F, 1.0F, 6.0F, 0.1F, new String[]{"Range", "r", "rang"})).withTag("range");
   protected final Value<Boolean> rotate = new Value(false, new String[]{"Rotate", "rotations"});
   protected final Value<Boolean> strictDirection = new Value(false, new String[]{"StrictDirection", "strictdir"});
   protected final EnumValue<AutoSwitch> swap;
   protected final NumberValue<Float> delay;
   protected final Value<Boolean> debug;
   protected final StopWatch timer;

   public PearlCatch() {
      super(new String[]{"PearlCatch", "pearlstop"}, Category.COMBAT);
      this.swap = new EnumValue(AutoSwitch.SILENT, new String[]{"Switch", "swap"});
      this.delay = new NumberValue(2.5F, 0.0F, 10.0F, 0.1F, new String[]{"PlaceDelay", "placedel", "delay"});
      this.debug = new Value(false, new String[]{"debug"});
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.range, this.rotate, this.strictDirection, this.swap, this.delay});
      if (PollosHook.isRunClient()) {
         this.getValues().add(this.debug);
      }

      this.debug.setValue(false);
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
   }
}
