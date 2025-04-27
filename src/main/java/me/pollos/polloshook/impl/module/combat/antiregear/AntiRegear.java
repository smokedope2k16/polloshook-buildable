package me.pollos.polloshook.impl.module.combat.antiregear;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.util.math.BlockPos;

public class AntiRegear extends ToggleableModule {
   protected final NumberValue<Float> enemyRange = (new NumberValue(8.0F, 1.0F, 15.0F, 0.1F, new String[]{"EnemyRange", "targetrange"})).withTag("range");
   protected final Value<Boolean> rotate = new Value(false, new String[]{"Rotations", "rotate", "rots"});
   protected final NumberValue<Float> breakRange = (new NumberValue(5.0F, 0.0F, 6.0F, 0.1F, new String[]{"BreakRange", "breakdistance", "range"})).withTag("range");
   protected final NumberValue<Float> breakDelay = new NumberValue(1.5F, 0.0F, 2.5F, 0.1F, new String[]{"BreakDelay", "delay"});
   protected BlockPos renderPos = null;
   protected final StopWatch renderTimer = new StopWatch();
   protected final StopWatch timer = new StopWatch();

   public AntiRegear() {
      super(new String[]{"AntiRegear", "noregear"}, Category.COMBAT);
      this.offerValues(new Value[]{this.enemyRange, this.rotate, this.breakRange, this.breakDelay});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerRender(this)});
   }

   protected void onToggle() {
      this.timer.reset();
   }
}
