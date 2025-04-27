package me.pollos.polloshook.impl.module.other.clickgui;


import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import me.pollos.polloshook.impl.gui.click.ClickGuiScreen;
import me.pollos.polloshook.impl.module.other.clickgui.mode.NumberStyle;

public class ClickGUI extends ToggleableModule {
   private final EnumValue<NumberStyle> numberButtonStyle;
   private final Value<Boolean> enumDropdown;
   private final Value<Boolean> colorByModule;
   private final Value<Boolean> homosexuality;
   private final Value<Boolean> futureBox;
   private final Value<Boolean> configs;
   private static ClickGUI CLICK_GUI;
   private ClickGuiScreen instance;

   public ClickGUI() {
      super(new String[]{"ClickGUI", "phobosgui", "pastedgui", "newgui", "guinew", "newergui", "colorpickergui"}, Category.OTHER);
      this.numberButtonStyle = new EnumValue(NumberStyle.FAT, new String[]{"NumberStyle", "buttonstyle", "numberbuttnostyle"});
      this.enumDropdown = new Value(false, new String[]{"EnumDropDown", "enummenu"});
      this.colorByModule = new Value(false, new String[]{"ColorByModule", "colorfrommodule"});
      this.homosexuality = new Value(false, new String[]{"DynamicRainbow", "dyn", "homo"});
      this.futureBox = new Value(false, new String[]{"FutureBox", "futureb"});
      this.configs = new Value(false, new String[]{"Configs", "configCategory"});
      this.offerValues(new Value[]{this.numberButtonStyle, this.enumDropdown, this.colorByModule, this.homosexuality, this.futureBox, this.configs});
      this.offerListeners(new Listener[]{new ListenerScreen(this)});
      setCLICK_GUI(this);
   }

   protected void onEnable() {
      if (mc.player != null && mc.world != null && !((IMinecraftClient)mc).isDisconnecting()) {
         if (!(mc.currentScreen instanceof ClickGuiScreen)) {
            ClickGuiScreen gui = new ClickGuiScreen();
            gui.init();
            gui.onGuiOpened();
            mc.setScreen(gui);
            this.instance = gui;
         }
      } else {
         this.setEnabled(false);
      }
   }

   protected void onDisable() {
      if (mc.player != null && mc.world != null) {
         if (mc.currentScreen instanceof ClickGuiScreen && this.instance != null) {
            this.instance.close();
            this.instance = null;
         }

      }
   }

   public static ClickGUI get() {
      return CLICK_GUI;
   }

   
   public EnumValue<NumberStyle> getNumberButtonStyle() {
      return this.numberButtonStyle;
   }

   
   public Value<Boolean> getEnumDropdown() {
      return this.enumDropdown;
   }

   
   public Value<Boolean> getColorByModule() {
      return this.colorByModule;
   }

   
   public Value<Boolean> getHomosexuality() {
      return this.homosexuality;
   }

   
   public Value<Boolean> getFutureBox() {
      return this.futureBox;
   }

   
   public Value<Boolean> getConfigs() {
      return this.configs;
   }

   
   public ClickGuiScreen getInstance() {
      return this.instance;
   }

   
   private static void setCLICK_GUI(ClickGUI CLICK_GUI) {
      ClickGUI.CLICK_GUI = CLICK_GUI;
   }
}
