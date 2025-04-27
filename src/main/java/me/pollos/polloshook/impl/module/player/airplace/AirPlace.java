package me.pollos.polloshook.impl.module.player.airplace;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.util.math.BlockPos;

public class AirPlace extends ToggleableModule {
   protected final Value<Boolean> onlyInLiquid = new Value(false, new String[]{"OnlyInLiquid", "liquid"});
   protected final NumberValue<Float> delay = new NumberValue(3.0F, 0.0F, 10.0F, 0.1F, new String[]{"Delay", "del"});
   protected final Value<Boolean> customRange = new Value(false, new String[]{"CustomRange", "customdistance"});
   protected final NumberValue<Float> maxRange;
   protected BlockPos pos;
   protected boolean cancel;
   protected final StopWatch timer;

   public AirPlace() {
      super(new String[]{"AirPlace", "air"}, Category.PLAYER);
      this.maxRange = (new NumberValue(5.0F, 0.0F, 6.0F, 0.1F, new String[]{"Range", "rang"})).withTag("range").setParent(this.customRange);
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.onlyInLiquid, this.delay, this.customRange, this.maxRange});
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerRender(this), new ListenerInteract(this)});
   }

   protected void onToggle() {
      this.cancel = false;
      this.pos = null;
   }
}
