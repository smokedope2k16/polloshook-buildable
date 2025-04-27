package me.pollos.polloshook.impl.module.other.hud;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.value.event.ValueEvent;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;
import me.pollos.polloshook.impl.module.other.colours.Colours;

public class HUD extends Module {
   protected final Value<Boolean> shadow = new Value(true, new String[]{"Shadow"});
   protected final Value<Boolean> openEditor = new Value(false, new String[]{"OpenEditor", "editor"});
   protected final NumberValue<Float> snapRadius = new NumberValue(5.0F, 1.0F, 10.0F, 0.5F, new String[]{"SnapRadius", "snaprad"});
   protected final NumberValue<Float> snapDelay = new NumberValue(2.0F, 1.0F, 3.5F, 0.25F, new String[]{"SnapDelay", "snapdel"});
   protected final Value<Boolean> rainbow = new Value(false, new String[]{"Rainbow", "rain"});
   protected final Value<Boolean> horizontal;
   protected final Value<Boolean> left;
   protected final Value<Boolean> gradient;
   protected final Value<Boolean> down;
   protected final NumberValue<Float> rainbowSpeed;
   protected final NumberValue<Float> rainbowOffset;
   protected final NumberValue<Integer> gradients;
   protected final ColorValue colorOne;
   protected final ColorValue colorTwo;
   protected final ColorValue colorThree;
   protected final ColorValue colorFour;
   protected boolean opened;
   protected static HUD INSTANCE;

   public HUD() {
      super(new String[]{"HUD", "hudelements"}, Category.OTHER);
      this.horizontal = (new Value(false, new String[]{"Horizontal", "horizontally"})).setParent(this.rainbow);
      this.left = (new Value(false, new String[]{"Left", "oppositeofright", "l"})).setParent(this.horizontal);
      this.gradient = (new Value(false, new String[]{"Gradient", "gradients"})).setParent(this.rainbow);
      this.down = (new Value(true, new String[]{"Down", "downwards"})).setParent(() -> {
         return (Boolean)this.rainbow.getValue() && !(Boolean)this.horizontal.getValue();
      });
      this.rainbowSpeed = (new NumberValue(2.5F, 1.0F, 7.5F, 0.1F, new String[]{"RainbowSpeed", "speed"})).setParent(this.rainbow);
      this.rainbowOffset = (new NumberValue(1.0F, 1.0F, 5.0F, 0.1F, new String[]{"RainbowOffset", "offset"})).setParent(this.rainbow);
      this.gradients = (new NumberValue(2, 2, 4, new String[]{"GradientsSize", "gradientsize"})).setParent(this.gradient);
      this.colorOne = (new ColorValue(new Color(16777215), true, new String[]{"FirstColor", "1st"})).setParent(() -> {
         return (Boolean)this.gradient.getValue() || !(Boolean)this.rainbow.getValue();
      });
      this.colorTwo = (new ColorValue(new Color(65535), false, new String[]{"SecondColor", "2nd"})).setParent(this.gradient);
      this.colorThree = (new ColorValue(new Color(16711935), false, new String[]{"ThirdColor", "3rd"})).setParent(() -> {
         return (Boolean)this.gradient.getValue() && (Integer)this.gradients.getValue() > 2;
      });
      this.colorFour = (new ColorValue(new Color(15773711), false, new String[]{"FourthColor", "4th"})).setParent(() -> {
         return (Boolean)this.gradient.getValue() && (Integer)this.gradients.getValue() > 3;
      });
      this.offerListeners(new Listener[]{new ListenerTick(this)});
      this.offerValues(new Value[]{this.shadow, this.openEditor, this.snapDelay, this.snapRadius, this.rainbow, this.horizontal, this.left, this.gradient, this.gradients, this.rainbowSpeed, this.rainbowOffset, this.down, this.colorOne, this.colorTwo, this.colorThree, this.colorFour});
      this.setDrawn(false);
      PollosHook.getEventBus().subscribe(this);
      this.openEditor.addObserver(this::onChange);
      INSTANCE = this;
   }

   public static HUD get() {
      return INSTANCE == null ? new HUD() : INSTANCE;
   }

   public static void runGlobalCheck() {
      List<ColorValue> colorValueList = Arrays.asList(get().getColorOne(), get().getColorThree(), get().getColorTwo(), get().getColorFour());
      if ((Boolean)Colours.get().getCopyFromHUD().getValue()) {
         colorValueList.forEach((cv) -> {
            cv.setGlobal(false).setGlobalAllowed(false);
         });
      } else {
         colorValueList.forEach((cv) -> {
            cv.setGlobalAllowed(true);
         });
      }

   }

   private void onChange(ValueEvent<Boolean> event) {
      if ((Boolean)event.getValue() && mc.player != null && mc.world != null) {
         mc.setScreen(new PollosHUD());
         this.openEditor.setValue(false);
         this.opened = true;
         event.setCanceled(true);
      }

   }

   public boolean drawShadows() {
      return (Boolean)this.shadow.getValue();
   }

   public void setFalse() {
      this.openEditor.setValue(false);
   }

   
   public Value<Boolean> getShadow() {
      return this.shadow;
   }

   
   public Value<Boolean> getOpenEditor() {
      return this.openEditor;
   }

   
   public NumberValue<Float> getSnapRadius() {
      return this.snapRadius;
   }

   
   public NumberValue<Float> getSnapDelay() {
      return this.snapDelay;
   }

   
   public Value<Boolean> getRainbow() {
      return this.rainbow;
   }

   
   public Value<Boolean> getHorizontal() {
      return this.horizontal;
   }

   
   public Value<Boolean> getLeft() {
      return this.left;
   }

   
   public Value<Boolean> getGradient() {
      return this.gradient;
   }

   
   public Value<Boolean> getDown() {
      return this.down;
   }

   
   public NumberValue<Float> getRainbowSpeed() {
      return this.rainbowSpeed;
   }

   
   public NumberValue<Float> getRainbowOffset() {
      return this.rainbowOffset;
   }

   
   public NumberValue<Integer> getGradients() {
      return this.gradients;
   }

   
   public ColorValue getColorOne() {
      return this.colorOne;
   }

   
   public ColorValue getColorTwo() {
      return this.colorTwo;
   }

   
   public ColorValue getColorThree() {
      return this.colorThree;
   }

   
   public ColorValue getColorFour() {
      return this.colorFour;
   }

   
   public boolean isOpened() {
      return this.opened;
   }
}
