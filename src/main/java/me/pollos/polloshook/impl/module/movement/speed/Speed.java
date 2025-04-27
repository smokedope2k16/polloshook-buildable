package me.pollos.polloshook.impl.module.movement.speed;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.movement.autowalk.AutoWalk;
import me.pollos.polloshook.impl.module.movement.speed.type.SpeedTypeEnum;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.input.Input;

public class Speed extends ToggleableModule {
   protected final EnumValue<SpeedTypeEnum> mode;
   protected final Value<Boolean> liquid;
   protected final Value<Boolean> useTimer;
   protected final Value<Boolean> strafeBoost;
   protected final NumberValue<Float> boostFactor;
   protected final StopWatch timer;

   public Speed() {
      super(new String[]{"Speed", "sped", "autospeed"}, Category.MOVEMENT);
      this.mode = new EnumValue(SpeedTypeEnum.STRAFE, new String[]{"Mode", "type", "m"});
      this.liquid = new Value(true, new String[]{"StopInLiquid", "stopinliquid", "water", "lava"});
      this.useTimer = new Value(false, new String[]{"UseTimer", "usertime", "timer"});
      this.strafeBoost = (new Value(false, new String[]{"StrafeBoost", "strafeboost"})).setParent(() -> {
         return ((SpeedTypeEnum)this.mode.getValue()).isStrafe();
      });
      this.boostFactor = (new NumberValue(1.2F, 0.1F, 2.5F, 0.1F, new String[]{"StrafeFactor", "boostfactor"})).setParent(this.strafeBoost);
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.mode, this.liquid, this.strafeBoost, this.boostFactor, this.useTimer});
      this.offerListeners(new Listener[]{new ListenerMotion(this), new ListenerMove(this), new ListenerEntityVelocity(this), new ListenerExplode(this), new ListenerPosLook(this)});
      this.useTimer.addObserver((o) -> {
         Managers.getTimerManager().reset();
      });
   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   protected void onEnable() {
      ((SpeedTypeEnum)this.mode.getValue()).getType().reset();
   }

   protected void onDisable() {
      Managers.getTimerManager().reset();
   }

   public Input getMovementInput() {
      Freecam FREECAM = (Freecam)Managers.getModuleManager().get(Freecam.class);
      AutoWalk AUTO_WALK = (AutoWalk)Managers.getModuleManager().get(AutoWalk.class);
      return FREECAM.isEnabled() && AUTO_WALK.isEnabled() ? AUTO_WALK.getInput() : mc.player.input;
   }
}
