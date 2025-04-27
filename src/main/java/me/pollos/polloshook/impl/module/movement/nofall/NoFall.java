package me.pollos.polloshook.impl.module.movement.nofall;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.nofall.mode.NoFallMode;

public class NoFall extends ToggleableModule {
   protected final NumberValue<Float> fallDistance = (new NumberValue(3.0F, 3.0F, 10.0F, 0.1F, new String[]{"FallDistance", "fallrange", "distance", "range", "r"})).withTag("range");
   protected final EnumValue<NoFallMode> mode;

   public NoFall() {
      super(new String[]{"NoFall", "nofalldamage", "nofalldmg"}, Category.MOVEMENT);
      this.mode = new EnumValue(NoFallMode.PACKET, new String[]{"Mode", "m", "type", "t"});
      this.offerValues(new Value[]{this.fallDistance, this.mode});
      this.offerListeners(new Listener[]{new ListenerPacket(this), new ListenerUpdate(this)});
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }
}
