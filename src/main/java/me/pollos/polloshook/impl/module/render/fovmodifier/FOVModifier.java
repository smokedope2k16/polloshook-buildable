package me.pollos.polloshook.impl.module.render.fovmodifier;

import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;

public class FOVModifier extends ToggleableModule {
   private final Value<Boolean> staticFov = new Value(false, new String[]{"Static", "staticfov", "statik"});
   private final Value<Boolean> noSubmerged = new Value(false, new String[]{"LiquidFix", "nosubmerged"});
   private final Value<Boolean> calculateLevel;
   private final NumberValue<Integer> spyglass;
   private final NumberValue<Integer> sprinting;
   private final NumberValue<Integer> swiftness;
   private final NumberValue<Integer> slowness;
   private final NumberValue<Integer> aim;
   private final NumberValue<Integer> fly;

   public FOVModifier() {
      super(new String[]{"FOVModifier", "fovmod"}, Category.RENDER);
      this.calculateLevel = (new Value(false, new String[]{"CalculateLevel", "levels"})).setParent(this.staticFov, true);
      this.spyglass = (new NumberValue(100, 0, 200, new String[]{"SpyGlass", "glass"})).setParent(this.staticFov, true).withTag("fovmodifier");
      this.sprinting = (new NumberValue(100, 0, 200, new String[]{"Sprinting", "sprint"})).setParent(this.staticFov, true).withTag("fovmodifier");
      this.swiftness = (new NumberValue(100, 0, 200, new String[]{"Speed", "swiftness"})).setParent(this.staticFov, true).withTag("fovmodifier");
      this.slowness = (new NumberValue(100, 0, 200, new String[]{"Slowness", "slow"})).setParent(this.staticFov, true).withTag("fovmodifier");
      this.aim = (new NumberValue(100, 0, 200, new String[]{"Aim", "aiming"})).setParent(this.staticFov, true).withTag("fovmodifier");
      this.fly = (new NumberValue(100, 0, 200, new String[]{"Fly", "flying"})).setParent(this.staticFov, true).withTag("fovmodifier");
      this.offerValues(new Value[]{this.staticFov, this.noSubmerged, this.calculateLevel, this.spyglass, this.sprinting, this.swiftness, this.slowness, this.fly, this.aim});
   }

   public boolean getStaticFOV() {
      return this.isEnabled() && (Boolean)this.staticFov.getValue();
   }

   public boolean getNoLavaFOV() {
      return this.isEnabled() && (Boolean)this.noSubmerged.getValue();
   }

   public boolean getCalculateLevel() {
      return (Boolean)this.calculateLevel.getValue();
   }

   public float getSprinting() {
      return (float)(Integer)this.sprinting.getValue() / 100.0F;
   }

   public float getSwiftness() {
      return (float)(Integer)this.swiftness.getValue() / 100.0F;
   }

   public float getSlowness() {
      return (float)(Integer)this.slowness.getValue() / 100.0F;
   }

   public float getAiming() {
      return (float)(Integer)this.aim.getValue() / 100.0F;
   }

   public float getSpy() {
      return (float)(Integer)this.spyglass.getValue() / 100.0F;
   }

   public float getFlying() {
      return (float)(Integer)this.fly.getValue() / 100.0F;
   }
}
