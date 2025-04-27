package me.pollos.polloshook.impl.module.movement.tickshift;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.tickshift.mode.TickShiftMode;

public class TickShift extends ToggleableModule {
   protected final EnumValue<TickShiftMode> mode;
   protected final Value<Boolean> velocityCheck;
   protected final NumberValue<Integer> maxTicks;
   protected final NumberValue<Integer> perTick;
   protected final NumberValue<Integer> chargeSpeed;
   protected final StopWatch timer;
   protected final StopWatch chargeTimer;
   protected boolean boosting;
   protected int boosted;

   public TickShift() {
      super(new String[]{"TickShift", "future", "warp", "shiftticks"}, Category.MOVEMENT);
      this.mode = new EnumValue(TickShiftMode.NORMAL, new String[]{"Mode", "type", "m"});
      this.velocityCheck = (new Value(false, new String[]{"VelocityCheck", "velocheck"})).setParent(this.mode, TickShiftMode.INSTANT);
      this.maxTicks = new NumberValue(20, 1, 40, new String[]{"MaxTicks", "maximumticks"});
      this.perTick = new NumberValue(4, 1, 10, new String[]{"PerTick", "ontick"});
      this.chargeSpeed = (new NumberValue(1, 1, 5, new String[]{"ChargeSpeed", "chargedelay"})).setParent(this.mode, TickShiftMode.NORMAL);
      this.timer = new StopWatch();
      this.chargeTimer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.velocityCheck, this.maxTicks, this.perTick, this.chargeSpeed});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
   }

   protected String getTag() {
      String tag = null;
      switch((TickShiftMode)this.mode.getValue()) {
      case INSTANT:
         tag = this.mode.getStylizedName();
         break;
      case NORMAL:
         tag = Integer.toString(this.boosted);
      }

      return tag;
   }

   protected void onToggle() {
      this.boosting = false;
      this.boosted = this.mode.getValue() == TickShiftMode.INSTANT ? (Integer)this.maxTicks.getValue() : 0;
   }

   
   public EnumValue<TickShiftMode> getMode() {
      return this.mode;
   }

   
   public boolean isBoosting() {
      return this.boosting;
   }

   
   public void setBoosting(boolean boosting) {
      this.boosting = boosting;
   }

   
   public int getBoosted() {
      return this.boosted;
   }
}
