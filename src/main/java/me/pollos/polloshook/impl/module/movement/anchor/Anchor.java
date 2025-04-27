package me.pollos.polloshook.impl.module.movement.anchor;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class Anchor extends ToggleableModule {
   protected final NumberValue<Integer> pitch = (new NumberValue(60, 0, 90, new String[]{"Pitch"})).withTag("degree");
   protected final Value<Boolean> doubles = new Value(true, new String[]{"2x1Holes", "doubles", "2x1"});
   protected final Value<Boolean> protocolSafe;
   protected final Value<Boolean> stopMotion;
   protected final Value<Boolean> fastFall;
   protected final NumberValue<Float> speed;
   protected boolean anchoring;

   public Anchor() {
      super(new String[]{"Anchor", "holepull", "nigger"}, Category.MOVEMENT);
      this.protocolSafe = (new Value(false, new String[]{"1.12.2Safe", "protocolsafe"})).setParent(this.doubles);
      this.stopMotion = new Value(true, new String[]{"StopMotion", "stopxz"});
      this.fastFall = new Value(true, new String[]{"FastFall", "stopy"});
      this.speed = (new NumberValue(1.0F, 1.0F, 5.0F, 0.25F, new String[]{"Speed", "sped", "s"})).setParent(this.fastFall);
      this.anchoring = false;
      this.offerValues(new Value[]{this.pitch, this.doubles, this.protocolSafe, this.stopMotion, this.fastFall, this.speed});
      this.offerListeners(new Listener[]{new ListenerMove(this)});
   }

   public boolean isAnchoring() {
      return this.isEnabled() && this.anchoring;
   }
}
