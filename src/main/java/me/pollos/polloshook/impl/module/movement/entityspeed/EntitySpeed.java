package me.pollos.polloshook.impl.module.movement.entityspeed;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class EntitySpeed extends ToggleableModule {
   protected final NumberValue<Float> speed = new NumberValue(2.2F, 1.0F, 10.0F, 0.1F, new String[]{"Speed", "sped"});
   protected final Value<Boolean> antiStuck = new Value(false, new String[]{"AntiStuck", "nostuck"});

   public EntitySpeed() {
      super(new String[]{"EntitySpeed", "espeed", "entitysped"}, Category.MOVEMENT);
      this.offerValues(new Value[]{this.speed, this.antiStuck});
      this.offerListeners(new Listener[]{new ListenerUpdate(this)});
   }

   protected String getTag() {
      return "%.1f".formatted(new Object[]{this.speed.getValue()});
   }
}
