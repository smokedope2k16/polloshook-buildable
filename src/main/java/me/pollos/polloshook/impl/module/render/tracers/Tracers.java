package me.pollos.polloshook.impl.module.render.tracers;

import java.awt.Color;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.module.render.tracers.mode.ColorMode;
import me.pollos.polloshook.impl.module.render.tracers.mode.TracersBone;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Tracers extends ToggleableModule {
   protected final Value<Boolean> offscreen = new Value(false, new String[]{"OffscreenOnly", "offscreen", "onlyoffscreen", "screen"});
   protected final Value<Boolean> yLevel = new Value(true, new String[]{"YLevel", "onlybelowy"});
   protected final NumberValue<Integer> yDistance;
   protected final NumberValue<Float> lineWidth;
   protected final EnumValue<TracersBone> bone;
   protected final NumberValue<Integer> opacity;
   protected final EnumValue<ColorMode> color;
   protected final ColorValue customColor;

   public Tracers() {
      super(new String[]{"Tracers", "tracers", "lines"}, Category.RENDER);
      this.yDistance = (new NumberValue(75, 0, 319, new String[]{"YDistance", "ylevel", "distance"})).withTag("range").setParent(this.yLevel);
      this.lineWidth = new NumberValue(1.0F, 1.0F, 4.0F, 0.1F, new String[]{"LineWidth", "width"});
      this.bone = new EnumValue(TracersBone.FEET, new String[]{"Bone", "part"});
      this.opacity = (new NumberValue(50, 0, 100, new String[]{"Opacity", "alpha"})).withTag("%");
      this.color = new EnumValue(ColorMode.AUTO, new String[]{"Color", "c", "colormode"});
      this.customColor = (new ColorValue(new Color(-1), true, new String[]{"CustomColor", "customc", "colorcustom"})).setParent(this.color, ColorMode.CUSTOM);
      this.offerValues(new Value[]{this.offscreen, this.yLevel, this.yDistance, this.lineWidth, this.bone, this.opacity, this.color, this.customColor});
      this.offerListeners(new Listener[]{new ListenerRender(this)});
   }

   protected Vec3d getDifference(Entity entity) {
      Vec3d interpolated = Interpolation.interpolateEntity(entity);
      return entity.getPos().subtract(interpolated);
   }

   
   public Value<Boolean> getOffscreen() {
      return this.offscreen;
   }

   
   public Value<Boolean> getYLevel() {
      return this.yLevel;
   }

   
   public NumberValue<Integer> getYDistance() {
      return this.yDistance;
   }

   
   public NumberValue<Float> getLineWidth() {
      return this.lineWidth;
   }

   
   public EnumValue<TracersBone> getBone() {
      return this.bone;
   }

   
   public NumberValue<Integer> getOpacity() {
      return this.opacity;
   }

   
   public EnumValue<ColorMode> getColor() {
      return this.color;
   }

   
   public ColorValue getCustomColor() {
      return this.customColor;
   }
}
