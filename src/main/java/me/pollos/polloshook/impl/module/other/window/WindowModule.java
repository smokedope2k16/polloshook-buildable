package me.pollos.polloshook.impl.module.other.window;

import java.awt.Color;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.system.WindowUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;

public class WindowModule extends Module {
   protected final ColorValue barColor = new ColorValue(new Color(0), false, new String[]{"BarColor", "barcol", "bar"});
   protected final ColorValue textColor = new ColorValue(new Color(-1), false, new String[]{"TextColor", "textcol", "text"});
   protected final ColorValue strokeColor = new ColorValue(new Color(1000), false, new String[]{"BorderColor", "strokecolor", "strokecol", "stroke"});
   protected final EnumValue<CornerMode> mode;

   public WindowModule() {
      super(new String[]{"Window", "customwindow", "291kmodule"}, Category.OTHER);
      this.mode = new EnumValue(CornerMode.BOX, new String[]{"Corner", "c"});
      this.offerValues(new Value[]{this.barColor, this.textColor, this.strokeColor, this.mode});
      this.offerListeners(new Listener[]{new ListenerTick(this)});
      this.onChange();
      PollosHook.getEventBus().subscribe(this);
   }

   public int getBarColor() {
      return WindowUtil.rgbToHex(this.barColor.getColor());
   }

   public int getTextColor() {
      return WindowUtil.rgbToHex(this.textColor.getColor());
   }

   public int getStrokeColor() {
      return WindowUtil.rgbToHex(this.strokeColor.getColor());
   }

   private void onChange() {
      this.barColor.addObserver((value) -> {
         this.apply();
      });
      this.textColor.addObserver((value) -> {
         this.apply();
      });
      this.strokeColor.addObserver((value) -> {
         this.apply();
      });
      this.mode.addObserver((value) -> {
         this.apply();
      });
   }

   public void apply() {
      WindowUtil.applyChanges(mc.getWindow(), ((CornerMode)this.mode.getValue()).getMode(), this.getBarColor(), this.getTextColor(), this.getStrokeColor());
   }
}
