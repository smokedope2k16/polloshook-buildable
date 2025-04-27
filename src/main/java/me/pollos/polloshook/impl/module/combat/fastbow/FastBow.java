package me.pollos.polloshook.impl.module.combat.fastbow;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class FastBow extends ToggleableModule {
   protected final NumberValue<Integer> ticks = new NumberValue(10, 3, 21, new String[]{"Ticks", "tick"});
   protected final Value<Boolean> tpsSync = new Value(false, new String[]{"TPSSync", "sync"});

   public FastBow() {
      super(new String[]{"FastBow", "autobowrelease", "autobow"}, Category.COMBAT);
      this.offerValues(new Value[]{this.ticks, this.tpsSync});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
   }
}
