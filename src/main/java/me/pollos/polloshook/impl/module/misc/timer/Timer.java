package me.pollos.polloshook.impl.module.misc.timer;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.manager.minecraft.movement.TimerManager;

public class Timer extends ToggleableModule {
   private final NumberValue<Float> timer = new NumberValue(1.0F, 0.1F, 50.0F, 0.1F, new String[]{"Timer", "time"});
   private final Value<Boolean> allowYield = new Value(true, new String[]{"AllowYield", "allowlock"});
   private final Value<Boolean> whileEating = new Value(false, new String[]{"WhileEating", "eating"});

   public Timer() {
      super(new String[]{"Timer", "time", "zoom"}, Category.MISC);
      this.offerValues(new Value[]{this.timer, this.allowYield, this.whileEating});
   }

   public void onTimerManagerTick() {
      TimerManager timerManger = Managers.getTimerManager();
      if (!timerManger.isYieldTimer() || !(Boolean)this.allowYield.getValue()) {
         if (!(Boolean)this.whileEating.getValue() && PlayerUtil.isEating()) {
            timerManger.reset();
         } else {
            timerManger.set((Float)this.timer.getValue());
         }

      }
   }

   protected String getTag() {
      return "%.1f".formatted(new Object[]{this.timer.getValue()});
   }

   public void onDisable() {
      Managers.getTimerManager().reset();
   }
}
