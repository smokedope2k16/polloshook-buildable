package me.pollos.polloshook.impl.module.combat.replenish;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.combat.replenish.mode.ClickMode;

public class Replenish extends ToggleableModule {
   protected final EnumValue<ClickMode> mode;
   protected final NumberValue<Integer> threshold;
   protected final NumberValue<Integer> delay;
   protected final Value<Boolean> openInv;
   protected boolean isServerInv;
   protected final StopWatch timer;

   public Replenish() {
      super(new String[]{"Replenish", "autostackfill", "stacker"}, Category.COMBAT);
      this.mode = new EnumValue(ClickMode.SHIFT_CLICK, new String[]{"Mode", "m", "t"});
      this.threshold = new NumberValue(20, 1, 30, new String[]{"Threshold", "thresh"});
      this.delay = new NumberValue(3, 0, 10, new String[]{"Delay", "del"});
      this.openInv = new Value(false, new String[]{"OpenInventory", "openinv"});
      this.isServerInv = false;
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.threshold, this.delay, this.openInv});
      this.offerListeners(new Listener[]{new ListenerGameLoop(this), new ListenerAction(this)});
      this.mode.addObserver((o) -> {
         this.observe((ClickMode)o.getValue());
      });
   }

   protected void onToggle() {
      this.isServerInv = false;
      this.timer.reset();
   }

   protected void observe(ClickMode mode) {
      if (mode == ClickMode.SWAP) {
         this.threshold.setMaximum(30);
         this.threshold.setValue((Integer)Math.min(30, (Integer)this.threshold.getValue()));
      } else {
         this.threshold.setMaximum(64);
      }

   }
}
