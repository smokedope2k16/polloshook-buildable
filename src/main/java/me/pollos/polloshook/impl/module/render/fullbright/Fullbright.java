package me.pollos.polloshook.impl.module.render.fullbright;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.fullbright.mode.FullbrightMode;
import net.minecraft.entity.effect.StatusEffects;

public class Fullbright extends ToggleableModule {
   protected final EnumValue<FullbrightMode> mode;
   protected final Value<Boolean> color;
   protected final ColorValue lightColor;

   public Fullbright() {
      super(new String[]{"Fullbright", "gamma", "brightness"}, Category.RENDER);
      this.mode = new EnumValue(FullbrightMode.NIGHT_VISION, new String[]{"Mode"});
      this.color = new Value(false, new String[]{"Colored", "coloured"});
      this.lightColor = (new ColorValue(new Color(1080259071, true), false, new String[]{"Color", "lightcolor"})).setParent(this.color);
      this.offerValues(new Value[]{this.mode, this.color, this.lightColor});
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerLightTexture(this)});
   }

   protected void onDisable() {
      switch((FullbrightMode)this.mode.getValue()) {
      case GAMMA:
         mc.options.getGamma().setValue(0.5D);
         break;
      case NIGHT_VISION:
         if (mc.player.getStatusEffect(StatusEffects.NIGHT_VISION) != null && ((FullbrightMode)this.mode.getValue()).equals(FullbrightMode.NIGHT_VISION)) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         }
      }

   }

   protected String getTag() {
      return this.mode.getStylizedName();
   }

   
   public EnumValue<FullbrightMode> getMode() {
      return this.mode;
   }

   
   public Value<Boolean> getColor() {
      return this.color;
   }

   
   public ColorValue getLightColor() {
      return this.lightColor;
   }
}