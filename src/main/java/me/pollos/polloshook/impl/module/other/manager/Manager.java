package me.pollos.polloshook.impl.module.other.manager;

import java.awt.Color;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.events.Event;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.thread.FPSThread;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.asm.ducks.IMinecraftClient;
import me.pollos.polloshook.impl.module.other.manager.util.ClientBrackets;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Manager extends Module {
   protected final EnumValue<ClientBrackets> brackets;
   protected final Value<Boolean> unicodeName;
   protected final StringValue clientName;
   protected final Value<Boolean> noNarrator;
   protected final Value<Boolean> noCrawl;
   protected final Value<Boolean> noSwim;
   protected final Value<Boolean> customDisplay;
   protected final StringValue display;
   protected final Value<Boolean> fpsInWindow;
   protected final Value<Boolean> memoryInWindow;
   protected final Value<Boolean> noUnfocusedSound;
   protected final Value<Boolean> syncFriends;
   protected final Value<Boolean> checkForFuture;
   protected final NumberValue<Float> fadeTime;
   protected final Value<Boolean> unfocusedCPU;
   protected final Value<Boolean> aspectRatioChanger;
   protected final NumberValue<Integer> height;
   protected final NumberValue<Integer> width;
   protected final Value<Boolean> smite;
   protected final Value<Boolean> loadColorConfigs;
   protected final Value<Boolean> keepInvCentered;
   protected final Value<Boolean> forgeHax;
   protected final ColorValue blocksColor;
   protected final ColorValue bracketColor;
   protected final ColorValue nameColor;
   protected final ColorValue indicatorColor;
   protected final ColorValue themeColor;
   private static Manager INSTANCE;
   private final FPSThread fpsCalcThread;

   public Manager() {
      super(new String[]{"Manager", "management", "compat"}, Category.OTHER);
      this.brackets = new EnumValue(ClientBrackets.BRACKET, new String[]{"Brackets", "bracket"});
      this.unicodeName = new Value(false, new String[]{"UnicodeName", "chickenname"});
      this.clientName = (new StringValue("polloshook", new String[]{"ClientName", "label"})).setParent(this.unicodeName, true);
      this.noNarrator = new Value(true, new String[]{"NoNarrator", "removenarrator"});
      this.noCrawl = new Value(false, new String[]{"NoCrawling", "nocrawl"});
      this.noSwim = new Value(false, new String[]{"NoSwim", "noswimming"});
      this.customDisplay = (new Value(false, new String[]{"CustomTitle", "customdisplay"})).setParent(() -> {
         return this.getFullScreenParent().isVisible();
      });
      this.display = (new StringValue("minecraft", new String[]{"DisplayName", "titlename"})).setParent(this.customDisplay).setTypingObserver(true);
      this.fpsInWindow = (new Value(false, new String[]{"FPSInWindow", "windowfps"})).setParent(() -> {
         return this.getFullScreenParent().isVisible();
      });
      this.memoryInWindow = (new Value(false, new String[]{"Memory%InWindow", "memorywindow", "memwindow"})).setParent(() -> {
         return this.getFullScreenParent().isVisible();
      });
      this.noUnfocusedSound = new Value(false, new String[]{"NoUnfocusedSounds", "unfocusedsounds"});
      this.syncFriends = new Value(false, new String[]{"SyncFriendCommands", "commandsync", "sync"});
      this.checkForFuture = (new Value(true, new String[]{"CheckForFuture", "futurecheck"})).setParent(this.syncFriends);
      this.fadeTime = new NumberValue(7.5F, 1.0F, 10.0F, 0.1F, new String[]{"FadeTime", "fade"});
      this.unfocusedCPU = new Value(false, new String[]{"UnfocusedCPU", "unfocusedfps"});
      this.aspectRatioChanger = new Value(false, new String[]{"AspectRatioChanger", "aspectratio"});
      this.height = (new NumberValue(mc.getWindow().getFramebufferHeight(), 0, mc.getWindow().getFramebufferHeight(), new String[]{"Height", "h"})).setParent(this.aspectRatioChanger);
      this.width = (new NumberValue(mc.getWindow().getFramebufferWidth(), 0, mc.getWindow().getFramebufferWidth(), new String[]{"Width", "w"})).setParent(this.aspectRatioChanger);
      this.smite = new Value(false, new String[]{"DeathSmite", "smite"});
      this.loadColorConfigs = new Value(false, new String[]{"LoadColorConfigs", "loadcolorconfig"});
      this.keepInvCentered = new Value(false, new String[]{"KeepInvCentered", "keepinvnetorycenter"});
      this.forgeHax = new Value(false, new String[]{"ForgeHax", "forgehacks", "forgeh4x"});
      this.blocksColor = new ColorValue(new Color(1509883904, true), true, new String[]{"BlocksColor", "blockscol"});
      this.bracketColor = new ColorValue(new Color(5592405), false, new String[]{"BracketColor", "bracketcol"});
      this.nameColor = new ColorValue(new Color(16777215), false, new String[]{"NameColor", "namecol"});
      this.indicatorColor = new ColorValue(new Color(6684927), false, new String[]{"IndicatorColor", "indicatorcol"});

      this.themeColor = new ColorValue(new Color(Formatting.DARK_PURPLE.getColorValue()), false, new String[]{"ThemeColor", "theme"});
      this.fpsCalcThread = new FPSThread();
      this.offerValues(new Value[]{this.brackets, this.unicodeName, this.clientName, this.noNarrator, this.noCrawl, this.noSwim, this.customDisplay, this.display, this.fpsInWindow, this.memoryInWindow, this.noUnfocusedSound, this.syncFriends, this.checkForFuture, this.fadeTime, this.unfocusedCPU, this.keepInvCentered, this.aspectRatioChanger, this.height, this.width, this.smite, this.loadColorConfigs, this.forgeHax, this.blocksColor, this.bracketColor, this.nameColor, this.indicatorColor, this.themeColor});
      this.setDrawn(false);
      this.customDisplay.addObserver((obs) -> {
         mc.getWindow().setTitle(this.getDisplayText());
      });
      this.display.addObserver((obs) -> {
         mc.getWindow().setTitle(this.getDisplayText());
      });
      this.fpsInWindow.addObserver((obs) -> {
         mc.getWindow().setTitle(this.getDisplayText());
      });
      this.offerListeners(new Listener[]{new ListenerTick(this), new ListenerFPS(this), new ListenerTabComplete(this), new ListenerSwim(this), new ListenerHeight(this), new ListenerWidth(this), new ListenerDeath(this)});
      PollosHook.getEventBus().subscribe(this);
      INSTANCE = this;
   }

   public Text getClientName() {
      String[] bracket = ((ClientBrackets)this.brackets.getValue()).getBrackets();
      Text left = Text.literal(bracket[0]).withColor(this.bracketColor.getColor().getRGB());
      Text label = Text.literal(this.getClientNameStr()).withColor(this.nameColor.getColor().getRGB());
      Text right = Text.literal(bracket[1]).withColor(this.bracketColor.getColor().getRGB());
      return left.copy().append(label.copy().append(right.copy()));
   }

   public SupplierParent getFullScreenParent() {
      if (mc.options.getFullscreen() == null) {
         boolean Factoid = true;
         return new SupplierParent(() -> {
            return Factoid;
         }, false);
      } else {
         return new SupplierParent(() -> {
            return (Boolean)mc.options.getFullscreen().getValue();
         }, true);
      }
   }

   public String getClientNameStr() {
      return (Boolean)this.unicodeName.getValue() ? "\ud83d\udc14" : (String)this.clientName.getValue();
   }

   public boolean getUnfocusedSound() {
      return (Boolean)this.noUnfocusedSound.getValue();
   }

   public boolean getNoNarrator() {
      return (Boolean)this.noNarrator.getValue();
   }

   public boolean stopCrawling() {
      return (Boolean)this.noCrawl.getValue();
   }

   public int getColorCode() {
      return this.indicatorColor.getColor().getRGB();
   }

   public Color getBlocksColor() {
      return this.blocksColor.getColor();
   }

   public float getFadeTime() {
      return (Float)this.fadeTime.getValue() * 100.0F;
   }

   public Text getThemedText(String string) {
      return Text.empty().append(string).withColor(this.themeColor.getColor().getRGB());
   }

   public String getDisplayText() {
      if ((Boolean)this.customDisplay.getValue()) {
         String var10000 = (String)this.display.getValue();
         return var10000 + this.getWindowAppendText();
      } else {
         IMinecraftClient imc = (IMinecraftClient)mc;
         return imc.$getWindowTitle();
      }
   }

   public String getWindowAppendText() {
      IMinecraftClient imc = (IMinecraftClient)mc;
      String limited = imc.is60FPSLimit() ? " (Limited)" : "";
      String fpsText = (Boolean)this.fpsInWindow.getValue() ? " - %dFPS%s".formatted(new Object[]{this.getFpsCalcThread().getFpsCount(), limited}) : "";
      long totalMemory = Runtime.getRuntime().totalMemory();
      long freeMemory = Runtime.getRuntime().freeMemory();
      long usedMemory = totalMemory - freeMemory;
      int per = (int)(usedMemory * 100L / totalMemory);
      String memoryText = (Boolean)this.memoryInWindow.getValue() ? " %dMB/%dMB (%d%%)".formatted(new Object[]{this.toMiB(usedMemory), this.toMiB(totalMemory), per}) : "";
      if ((Boolean)this.fpsInWindow.getValue() && (Boolean)this.memoryInWindow.getValue()) {
         return fpsText + " |" + memoryText;
      } else {
         return (Boolean)this.memoryInWindow.getValue() ? " -" + memoryText : fpsText;
      }
   }

   public void sendMessage(String name, boolean remove) {
      if ((Boolean)this.syncFriends.getValue()) {
         if (!(Boolean)this.checkForFuture.getValue() || PollosHook.isFuture()) {
            if (!PlayerUtil.isNull()) {
               String cmd = "." + (remove ? "remove " : "add ");
               mc.player.networkHandler.sendChatMessage(cmd + name);
            }
         }
      }
   }

   private long toMiB(long bytes) {
      return bytes / 1024L / 1024L;
   }

   public static Manager get() {
      return INSTANCE == null ? (INSTANCE = new Manager()) : INSTANCE;
   }

   
   public Value<Boolean> getCustomDisplay() {
      return this.customDisplay;
   }

   
   public Value<Boolean> getFpsInWindow() {
      return this.fpsInWindow;
   }

   
   public Value<Boolean> getMemoryInWindow() {
      return this.memoryInWindow;
   }

   
   public Value<Boolean> getLoadColorConfigs() {
      return this.loadColorConfigs;
   }

   
   public Value<Boolean> getKeepInvCentered() {
      return this.keepInvCentered;
   }

   
   public Value<Boolean> getForgeHax() {
      return this.forgeHax;
   }

   
   public ColorValue getThemeColor() {
      return this.themeColor;
   }

   
   public FPSThread getFpsCalcThread() {
      return this.fpsCalcThread;
   }

   public static class AspectRatioWidthEvent extends Event {
      private float width;

      
      public float getWidth() {
         return this.width;
      }

      
      public void setWidth(float width) {
         this.width = width;
      }

      
      private AspectRatioWidthEvent(float width) {
         this.width = width;
      }

      
      public static Manager.AspectRatioWidthEvent of(float width) {
         return new Manager.AspectRatioWidthEvent(width);
      }
   }

   public static class AspectRatioHeightEvent extends Event {
      private float height;

      
      public float getHeight() {
         return this.height;
      }

      
      public void setHeight(float height) {
         this.height = height;
      }

      
      private AspectRatioHeightEvent(float height) {
         this.height = height;
      }

      
      public static Manager.AspectRatioHeightEvent of(float height) {
         return new Manager.AspectRatioHeightEvent(height);
      }
   }
}