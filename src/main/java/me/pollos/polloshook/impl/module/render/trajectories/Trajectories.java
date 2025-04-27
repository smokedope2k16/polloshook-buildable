package me.pollos.polloshook.impl.module.render.trajectories;

import java.awt.Color;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class Trajectories extends ToggleableModule {
   protected final NumberValue<Integer> offsetPitch = (new NumberValue(42, -90, 90, new String[]{"OffsetPitch", "poffset"})).withTag("degree");
   protected final NumberValue<Integer> offset = (new NumberValue(90, -90, 90, new String[]{"Offset", "off"})).withTag("degree");
   protected final Value<Boolean> roundUp = new Value(false, new String[]{"RoundUp", "round"});
   protected final Value<Boolean> requireActiveHand = new Value(false, new String[]{"RequireActiveHand", "requirebowshot", "bowshot"});
   protected final ColorValue color = new ColorValue(new Color(255), true, new String[]{"Color", "c"});
   protected final ColorValue entityColor = new ColorValue(new Color(267386880), true, new String[]{"EntityColor", "hitcolor", "hitc"});
   protected final Value<Boolean> expTest = new Value(false, new String[]{"ExpTest"});

   public Trajectories() {
      super(new String[]{"Trajectories", "tracjetory"}, Category.RENDER);
      this.offerValues(new Value[]{this.offsetPitch, this.offset, this.roundUp, this.requireActiveHand, this.color, this.entityColor, this.expTest});
      this.offerListeners(new Listener[]{new ListenerRender(this)});
   }
}
