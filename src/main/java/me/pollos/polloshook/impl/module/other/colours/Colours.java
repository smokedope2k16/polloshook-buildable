package me.pollos.polloshook.impl.module.other.colours;

import java.awt.Color;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;
import me.pollos.polloshook.impl.module.other.hud.HUD;

public class Colours extends Module {
   private final Value<Boolean> copyFromHUD = new Value(false, new String[]{"CopyFromHUD", "hud"});
   private final ColorValue mainColor = (new ColorValue(new Color(40863), false, new String[]{"Color", "c", "maincolor"})).setParent((Parent)this.getColorParent());
   private final Value<Boolean> rainbow;
   private final ColorValue friendColor;
   private final NumberValue<Float> rainbowSpeed;
   private final NumberValue<Float> rainbowSaturation;
   private final NumberValue<Float> rainbowLightness;
   private static Colours COLOURS;

   public Colours() {
      super(new String[]{"Colours", "color", "colors"}, Category.OTHER);
      this.rainbow = (new Value(false, new String[]{"Rainbow", "pridemonth"})).setParent(this.copyFromHUD, true);
      this.friendColor = new ColorValue(new Color(7405567), false, new String[]{"FriendColor", "friendcol"});
      this.rainbowSpeed = new NumberValue(1.0F, 0.1F, 10.0F, 0.1F, new String[]{"RainbowSpeed", "speed"});
      this.rainbowSaturation = new NumberValue(100.0F, 0.0F, 100.0F, 1.0F, new String[]{"RainbowSaturation", "rs"});
      this.rainbowLightness = new NumberValue(72.0F, 0.0F, 100.0F, 1.0F, new String[]{"RainbowLightness", "rl"});
      this.offerValues(new Value[]{this.copyFromHUD, this.mainColor, this.rainbow, this.friendColor, this.rainbowSpeed, this.rainbowSaturation, this.rainbowLightness});
      this.copyFromHUD.addObserver((o) -> {
         if ((Boolean)o.getValue()) {
            HUD.runGlobalCheck();
         }

      });
      this.setDrawn(false);
      PollosHook.getEventBus().subscribe(this);
      COLOURS = this;
   }

   public static Colours get() {
      return COLOURS;
   }

   public int getColorRGB() {
      return this.getColor().getRGB();
   }

   public Color getColor() {
      return this.getColor(0);
   }

   public Color getColor(int offset) {
      if ((Boolean)this.copyFromHUD.getValue()) {
         HUD.runGlobalCheck();
         return HUDModule.GLOBAL_ELEMENT.getColor(offset);
      } else {
         return (Boolean)this.rainbow.getValue() ? this.getRainbow(offset) : this.mainColor.getColor();
      }
   }

   public Color getFriendColor() {
      return this.friendColor.getColor();
   }

   public Color getRainbow(int offset) {
      return this.getRainbow(offset, (Float)this.rainbowSpeed.getValue());
   }

   public Color getRainbow(int offset, float speed) {
      return ColorUtil.rainbow(speed, (float)offset, (Float)this.rainbowSaturation.getValue(), (Float)this.rainbowLightness.getValue());
   }

   public Color getColourCustomAlpha(int alpha) {
      return ColorUtil.changeAlpha(this.getColor(), alpha);
   }

   private SupplierParent getColorParent() {
      return new SupplierParent(() -> {
         return !(Boolean)this.rainbow.getValue() && !(Boolean)this.copyFromHUD.getValue();
      }, false);
   }

   
   public Value<Boolean> getCopyFromHUD() {
      return this.copyFromHUD;
   }

   
   public NumberValue<Float> getRainbowSaturation() {
      return this.rainbowSaturation;
   }

   
   public NumberValue<Float> getRainbowLightness() {
      return this.rainbowLightness;
   }
}
