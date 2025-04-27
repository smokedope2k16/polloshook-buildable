package me.pollos.polloshook.impl.module.movement.invwalk;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.gui.click.ClickGuiScreen;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;

public class InvWalk extends ToggleableModule {
   protected final Value<Boolean> inventoryOnly = new Value(false, new String[]{"InventoryOnly", "inventory"});
   protected final Value<Boolean> includeContainers;
   protected final Value<Boolean> sneak;
   protected final Value<Boolean> flyingCheck;
   protected final Value<Boolean> rotate;
   protected final Value<Boolean> inventoryFill;
   protected final StopWatch timer;

   public InvWalk() {
      super(new String[]{"InvWalk", "inventorymove"}, Category.MOVEMENT);
      this.includeContainers = (new Value(false, new String[]{"IncludeContainers", "containers"})).setParent(this.inventoryOnly);
      this.sneak = new Value(false, new String[]{"Sneak"});
      this.flyingCheck = (new Value(false, new String[]{"OnlyIfFlying", "flying"})).setParent(this.sneak);
      this.rotate = new Value(false, new String[]{"Rotate", "rotations"});
      this.inventoryFill = new Value(false, new String[]{"InventoryFill", "invfill"});
      this.timer = new StopWatch();
      this.offerListeners(new Listener[]{new ListenerUpdate(this), new ListenerMotion(this)});
      this.offerValues(new Value[]{this.inventoryOnly, this.includeContainers, this.sneak, this.flyingCheck, this.rotate, this.inventoryFill});
   }

   protected boolean isValidScreen(Screen screen) {
      if (!(screen instanceof ChatScreen) && screen != null) {
         List<Class<?>> screenList = Arrays.asList(CreativeInventoryScreen.class, InventoryScreen.class, GameMenuScreen.class, HorseScreen.class, ClickGuiScreen.class, PollosHUD.class);
         List<Class<?>> containerList = Arrays.asList(BeaconScreen.class, BlastFurnaceScreen.class, BrewingStandScreen.class, CartographyTableScreen.class, CraftingScreen.class, EnchantmentScreen.class, FurnaceScreen.class, GrindstoneScreen.class, HopperScreen.class, LecternScreen.class, LoomScreen.class, MerchantScreen.class, ShulkerBoxScreen.class, SmithingScreen.class, SmokerScreen.class, StonecutterScreen.class);
         boolean inv = mc.player.currentScreenHandler instanceof GenericContainerScreenHandler;
         boolean shulk = mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler;
         boolean container = (Boolean)this.includeContainers.getValue() && inv || shulk || containerList.contains(screen.getClass());
         boolean screenOrContainer = container || screenList.contains(screen.getClass());
         return (Boolean)this.inventoryOnly.getValue() && !screenOrContainer ? screen instanceof InventoryScreen : true;
      } else {
         return false;
      }
   }
}
