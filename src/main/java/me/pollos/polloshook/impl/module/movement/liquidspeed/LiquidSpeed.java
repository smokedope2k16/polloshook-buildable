package me.pollos.polloshook.impl.module.movement.liquidspeed;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;

public class LiquidSpeed extends ToggleableModule {
   protected final Value<Boolean> water = new Value(true, new String[]{"Water", "w", "wet"});
   protected final Value<Boolean> cancelSwimming;
   protected final Value<Boolean> depthStrider;
   protected final NumberValue<Float> waterSpeed;
   protected final Value<Boolean> lava;
   protected final NumberValue<Float> lavaSpeed;
   protected final Value<Boolean> elytraFly;
   protected final NumberValue<Float> ySpeedBoost;
   protected final NumberValue<Float> elytraSpeed;
   protected final Value<Boolean> ySpeed;
   protected final NumberValue<Float> heightDown;
   protected final NumberValue<Float> heightUp;
   protected final StopWatch timer;

   public LiquidSpeed() {
      super(new String[]{"LiquidSpeed", "fluidspeed", "fastswim", "lavaspeed"}, Category.MOVEMENT);
      this.cancelSwimming = (new Value(false, new String[]{"CancelSwimming", "cancelswim"})).setParent(this.water);
      this.depthStrider = (new Value(true, new String[]{"DepthStrider", "strider", "s"})).setParent(this.water);
      this.waterSpeed = (new NumberValue(1.0F, 0.1F, 5.0F, 0.1F, new String[]{"WaterSpeed", "watersped"})).setParent(this.water);
      this.lava = new Value(true, new String[]{"Lava", "lavaspeed"});
      this.lavaSpeed = (new NumberValue(1.0F, 0.1F, 5.0F, 0.1F, new String[]{"LavaSpeed", "lava"})).setParent(this.lava);
      this.elytraFly = (new Value(false, new String[]{"ElytraFly", "efly", "elytra"})).setParent(this.lava);
      this.ySpeedBoost = (new NumberValue(0.5F, 0.0F, 1.0F, 0.1F, new String[]{"YSpeedBoost"})).setParent(() -> {
         return this.ySpeedBoostParent().isVisible();
      });
      this.elytraSpeed = (new NumberValue(2.5F, 0.0F, 10.0F, 0.1F, new String[]{"ElytraSpeed", "espeed", "speed"})).setParent(this.elytraFly);
      this.ySpeed = new Value(false, new String[]{"YSpeed", "ysped"});
      this.heightDown = (new NumberValue(0.3F, 0.1F, 2.5F, 0.1F, new String[]{"-Height", "+h"})).setParent(this.ySpeed);
      this.heightUp = (new NumberValue(0.1F, 0.1F, 2.5F, 0.1F, new String[]{"+Height", "h"})).setParent(this.ySpeed);
      this.timer = new StopWatch();
      this.offerValues(new Value[]{this.water, this.cancelSwimming, this.depthStrider, this.waterSpeed, this.lava, this.lavaSpeed, this.elytraFly, this.ySpeedBoost, this.elytraSpeed, this.ySpeed, this.heightDown, this.heightUp});
      this.offerListeners(new Listener[]{new ListenerMove(this), new ListenerSwimming(this), new ListenerPosLook(this)});
   }

   private SupplierParent ySpeedBoostParent() {
      return new SupplierParent(() -> {
         return (Boolean)this.ySpeed.getValue() && (Boolean)this.elytraFly.getValue() && this.elytraFly.getParent().isVisible();
      }, false);
   }
}
