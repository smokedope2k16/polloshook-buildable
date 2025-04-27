package me.pollos.polloshook.impl.module.combat.selfblocker;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.TrapModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class SelfBlocker extends TrapModule {
   protected final Value<Boolean> autoDisable = new Value(false, new String[]{"AutoDisable", "disable"});
   protected final Value<Boolean> auto = new Value(false, new String[]{"Auto", "keyCodec", "automatic"});
   protected final NumberValue<Float> range;

   public SelfBlocker() {
      super(new String[]{"SelfBlocker", "selftrap", "selfplace"}, Category.COMBAT);
      this.range = (new NumberValue(3.5F, 1.0F, 6.0F, 0.1F, new String[]{"Range", "rang", "autorange"})).withTag("range").setParent(this.auto);
      this.offerValues(new Value[]{this.autoDisable, this.auto, this.range});
      this.offerListeners(new Listener[]{new ListenerMotion(this)});
      this.top.setValue(false);
      this.feet.setValue(true);
      this.getValues().remove(this.feet);
      this.floor.setValue(true);
      this.getValues().remove(this.floor);
   }
}
