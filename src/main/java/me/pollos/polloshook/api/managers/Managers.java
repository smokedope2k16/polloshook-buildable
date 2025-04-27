package me.pollos.polloshook.api.managers;

import java.util.Arrays;
import java.util.List;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.impl.manager.config.ConfigManager;
import me.pollos.polloshook.impl.manager.friend.FriendManager;
import me.pollos.polloshook.impl.manager.internal.CommandManager;
import me.pollos.polloshook.impl.manager.internal.ModuleManager;
import me.pollos.polloshook.impl.manager.internal.ShaderManager;
import me.pollos.polloshook.impl.manager.internal.TextManager;
import me.pollos.polloshook.impl.manager.irc.IrcManager;
import me.pollos.polloshook.impl.manager.macro.MacroManager;
import me.pollos.polloshook.impl.manager.minecraft.block.BlocksManager;
import me.pollos.polloshook.impl.manager.minecraft.combat.EntitiesManager;
import me.pollos.polloshook.impl.manager.minecraft.combat.PearlManager;
import me.pollos.polloshook.impl.manager.minecraft.combat.PopManager;
import me.pollos.polloshook.impl.manager.minecraft.combat.SafeManager;
import me.pollos.polloshook.impl.manager.minecraft.combat.potion.PotionManager;
import me.pollos.polloshook.impl.manager.minecraft.connection.ConnectionManager;
import me.pollos.polloshook.impl.manager.minecraft.movement.PositionManager;
import me.pollos.polloshook.impl.manager.minecraft.movement.RotationManager;
import me.pollos.polloshook.impl.manager.minecraft.movement.TimerManager;
import me.pollos.polloshook.impl.manager.minecraft.movement.speed.SpeedManager;
import me.pollos.polloshook.impl.manager.minecraft.server.CrashManager;
import me.pollos.polloshook.impl.manager.minecraft.server.ExtrapolationManager;
import me.pollos.polloshook.impl.manager.minecraft.server.InventoryManager;
import me.pollos.polloshook.impl.manager.minecraft.server.ServerManager;
import me.pollos.polloshook.impl.manager.minecraft.server.TpsManager;

public class Managers {
   private static final CommandManager COMMAND_MANAGER = (new CommandManager()).start("Loading Command Manager...").finish("Finished loading Command Manager");
   private static final ModuleManager MODULE_MANAGER = (new ModuleManager()).start("Loading Module Manager...").finish("Finished loading Module Manager");
   private static final ConfigManager CONFIG_MANAGER = (new ConfigManager()).start("Loading Config Manager...").finish("Finished loading Config Manager");
   private static final MacroManager MACRO_MANAGER = (new MacroManager()).start("Loading Macro Manager...").finish("Finished loading Macro Manager");
   private static final FriendManager FRIEND_MANAGER = (new FriendManager()).start("Loading Friend Manager...").finish("Finished loading Friend Manager");
   private static final PopManager POP_MANAGER = new PopManager();
   private static final TimerManager TIMER_MANAGER = new TimerManager();
   private static final TpsManager TPS_MANAGER = new TpsManager();
   private static final SpeedManager SPEED_MANAGER = new SpeedManager();
   private static final RotationManager ROTATION_MANAGER = new RotationManager();
   private static final PearlManager PEARL_MANAGER = new PearlManager();
   private static final PositionManager POSITION_MANAGER = new PositionManager();
   private static final ServerManager SERVER_MANAGER = new ServerManager();
   private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
   private static final SafeManager SAFE_MANAGER = new SafeManager();
   private static final CrashManager CRASH_MANAGER = new CrashManager();
   private static final IrcManager IRC_MANAGER = new IrcManager();
   private static final BlocksManager BLOCKS_MANAGER = new BlocksManager();
   private static final EntitiesManager ENTITIES_MANAGER = new EntitiesManager();
   private static final InventoryManager INVENTORY_MANAGER = new InventoryManager();
   private static final ExtrapolationManager EXTRAPOLATION_MANAGER = new ExtrapolationManager();
   private static final TextManager TEXT_MANAGER = new TextManager();
   private static final PotionManager POTION_MANAGER = new PotionManager();
   private static final ShaderManager SHADER_MANAGER = new ShaderManager();

   public static void init() {
      List<SubscriberImpl> subscribeQueue = Arrays.asList(MODULE_MANAGER, COMMAND_MANAGER, POP_MANAGER, TPS_MANAGER, SPEED_MANAGER, TIMER_MANAGER, ROTATION_MANAGER, PEARL_MANAGER, POSITION_MANAGER, SERVER_MANAGER, CONNECTION_MANAGER, MACRO_MANAGER, SAFE_MANAGER, CRASH_MANAGER, BLOCKS_MANAGER, ENTITIES_MANAGER, INVENTORY_MANAGER, EXTRAPOLATION_MANAGER, POTION_MANAGER);
      subscribeQueue.forEach((sub) -> {
         PollosHook.getEventBus().subscribe(sub);
      });
      TEXT_MANAGER.init();
      CONFIG_MANAGER.init();
      COMMAND_MANAGER.init();
      MACRO_MANAGER.init();
      MODULE_MANAGER.init();
      FRIEND_MANAGER.init();
   }

   public static ModuleManager getModuleManager() {
      return MODULE_MANAGER;
   }

   public static CommandManager getCommandManager() {
      return COMMAND_MANAGER;
   }

   public static PopManager getPopManager() {
      return POP_MANAGER;
   }

   public static ConfigManager getConfigManager() {
      return CONFIG_MANAGER;
   }

   public static FriendManager getFriendManager() {
      return FRIEND_MANAGER;
   }

   public static TpsManager getTpsManager() {
      return TPS_MANAGER;
   }

   public static SpeedManager getSpeedManager() {
      return SPEED_MANAGER;
   }

   public static TimerManager getTimerManager() {
      return TIMER_MANAGER;
   }

   public static RotationManager getRotationManager() {
      return ROTATION_MANAGER;
   }

   public static PositionManager getPositionManager() {
      return POSITION_MANAGER;
   }

   public static MacroManager getMacroManager() {
      return MACRO_MANAGER;
   }

   public static SafeManager getSafeManager() {
      return SAFE_MANAGER;
   }

   public static IrcManager getIrcManager() {
      return IRC_MANAGER;
   }

   public static BlocksManager getBlocksManager() {
      return BLOCKS_MANAGER;
   }

   public static EntitiesManager getEntitiesManager() {
      return ENTITIES_MANAGER;
   }

   public static InventoryManager getInventoryManager() {
      return INVENTORY_MANAGER;
   }

   public static ExtrapolationManager getExtrapolationManager() {
      return EXTRAPOLATION_MANAGER;
   }

   public static TextManager getTextManager() {
      return TEXT_MANAGER;
   }

   public static ServerManager getServerManager() {
      return SERVER_MANAGER;
   }

   public static PotionManager getPotionManager() {
      return POTION_MANAGER;
   }

   public static ShaderManager getShaderManager() {
      return SHADER_MANAGER;
   }
}
