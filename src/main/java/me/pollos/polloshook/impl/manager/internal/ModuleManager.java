package me.pollos.polloshook.impl.manager.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Initializable;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.util.binds.keyboard.hold.HoldKeybind;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyPressAction;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.binds.mouse.MouseButton;
import me.pollos.polloshook.api.util.binds.mouse.MouseClickAction;
import me.pollos.polloshook.api.util.binds.mouse.MouseUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.system.SystemStatus;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import me.pollos.polloshook.impl.config.modules.HudConfig;
import me.pollos.polloshook.impl.config.modules.SimpleConfig;
import me.pollos.polloshook.impl.events.keyboard.KeyPressEvent;
import me.pollos.polloshook.impl.events.keyboard.MouseClickEvent;
import me.pollos.polloshook.impl.module.combat.antiregear.AntiRegear;
import me.pollos.polloshook.impl.module.combat.aura.Aura;
import me.pollos.polloshook.impl.module.combat.autoarmour.AutoArmour;
import me.pollos.polloshook.impl.module.combat.autocrystal.AutoCrystal;
import me.pollos.polloshook.impl.module.combat.autoexp.AutoExp;
import me.pollos.polloshook.impl.module.combat.autofeetplace.AutoFeetPlace;
import me.pollos.polloshook.impl.module.combat.autolog.AutoLog;
import me.pollos.polloshook.impl.module.combat.autototem.AutoTotem;
import me.pollos.polloshook.impl.module.combat.autotrap.AutoTrap;
import me.pollos.polloshook.impl.module.combat.autoweb.AutoWeb;
import me.pollos.polloshook.impl.module.combat.blocker.Blocker;
import me.pollos.polloshook.impl.module.combat.criticals.Criticals;
import me.pollos.polloshook.impl.module.combat.fastbow.FastBow;
import me.pollos.polloshook.impl.module.combat.holefill.HoleFill;
import me.pollos.polloshook.impl.module.combat.idpredict.IDPredict;
import me.pollos.polloshook.impl.module.combat.pollosresolver.PollosResolver;
import me.pollos.polloshook.impl.module.combat.projectilemanip.ProjectileManip;
import me.pollos.polloshook.impl.module.combat.replenish.Replenish;
import me.pollos.polloshook.impl.module.combat.selfblocker.SelfBlocker;
import me.pollos.polloshook.impl.module.combat.selffill.SelfFill;
import me.pollos.polloshook.impl.module.misc.announcer.Announcer;
import me.pollos.polloshook.impl.module.misc.antiafk.AntiAFK;
import me.pollos.polloshook.impl.module.misc.antiaim.AntiAim;
import me.pollos.polloshook.impl.module.misc.antihitbox.AntiHitbox;
import me.pollos.polloshook.impl.module.misc.antiinteract.AntiInteract;
import me.pollos.polloshook.impl.module.misc.autoreconnect.AutoReconnect;
import me.pollos.polloshook.impl.module.misc.autoreply.AutoReply;
import me.pollos.polloshook.impl.module.misc.autorespawn.AutoRespawn;
import me.pollos.polloshook.impl.module.misc.chatappend.ChatAppend;
import me.pollos.polloshook.impl.module.misc.deathcoordslog.DeathCoordsLog;
import me.pollos.polloshook.impl.module.misc.middleclick.MiddleClick;
import me.pollos.polloshook.impl.module.misc.nameprotect.NameProtect;
import me.pollos.polloshook.impl.module.misc.nobreakanim.NoBreakAnim;
import me.pollos.polloshook.impl.module.misc.noquitdesync.NoQuitDesync;
import me.pollos.polloshook.impl.module.misc.nosoundlag.NoSoundLag;
import me.pollos.polloshook.impl.module.misc.packetlogger.PacketLogger;
import me.pollos.polloshook.impl.module.misc.payloadspoof.PayloadSpoof;
import me.pollos.polloshook.impl.module.misc.pingspoof.PingSpoof;
import me.pollos.polloshook.impl.module.misc.popcounter.PopCounter;
import me.pollos.polloshook.impl.module.misc.pvpinfo.PvPInfo;
import me.pollos.polloshook.impl.module.misc.spammer.Spammer;
import me.pollos.polloshook.impl.module.misc.swing.Swing;
import me.pollos.polloshook.impl.module.misc.timer.Timer;
import me.pollos.polloshook.impl.module.misc.visualrange.VisualRange;
import me.pollos.polloshook.impl.module.movement.anchor.Anchor;
import me.pollos.polloshook.impl.module.movement.autowalk.AutoWalk;
import me.pollos.polloshook.impl.module.movement.boatfly.BoatFly;
import me.pollos.polloshook.impl.module.movement.elytrafly.ElytraFly;
import me.pollos.polloshook.impl.module.movement.entitycontrol.EntityControl;
import me.pollos.polloshook.impl.module.movement.entityspeed.EntitySpeed;
import me.pollos.polloshook.impl.module.movement.fly.Fly;
import me.pollos.polloshook.impl.module.movement.holesnap.HoleSnap;
import me.pollos.polloshook.impl.module.movement.icespeed.IceSpeed;
import me.pollos.polloshook.impl.module.movement.invwalk.InvWalk;
import me.pollos.polloshook.impl.module.movement.jesus.Jesus;
import me.pollos.polloshook.impl.module.movement.liquidspeed.LiquidSpeed;
import me.pollos.polloshook.impl.module.movement.longjump.LongJump;
import me.pollos.polloshook.impl.module.movement.noaccel.NoAccel;
import me.pollos.polloshook.impl.module.movement.nofall.NoFall;
import me.pollos.polloshook.impl.module.movement.noslow.NoSlow;
import me.pollos.polloshook.impl.module.movement.phase.Phase;
import me.pollos.polloshook.impl.module.movement.reversestep.ReverseStep;
import me.pollos.polloshook.impl.module.movement.speed.Speed;
import me.pollos.polloshook.impl.module.movement.step.Step;
import me.pollos.polloshook.impl.module.movement.tickshift.TickShift;
import me.pollos.polloshook.impl.module.movement.tridentfly.TridentFly;
import me.pollos.polloshook.impl.module.movement.velocity.Velocity;
import me.pollos.polloshook.impl.module.other.capes.Capes;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.other.fastlatency.FastLatency;
import me.pollos.polloshook.impl.module.other.font.CustomFont;
import me.pollos.polloshook.impl.module.other.hud.HUD;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.armor.Armor;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.coords.Coords;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.health.Health;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.hotbarkeys.HotbarKeys;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.info.Info;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.totems.Totems;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.armorwarning.ArmorWarning;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.arraylist.Arraylist;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.clientmessages.ClientMessages;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.dotgod.DotGod;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.inventory.Inventory;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.lagnotify.LagNotify;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.pearlcooldown.PearlCooldown;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.textradar.TextRadar;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.velocitygraph.VelocityGraph;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.watermark.Watermark;
import me.pollos.polloshook.impl.module.other.hud.elements.draggable.welcomer.Welcomer;
import me.pollos.polloshook.impl.module.other.irc.IrcModule;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.other.rpc.RPC;
import me.pollos.polloshook.impl.module.other.window.WindowModule;
import me.pollos.polloshook.impl.module.player.airplace.AirPlace;
import me.pollos.polloshook.impl.module.player.automine.AutoMine;
import me.pollos.polloshook.impl.module.player.autotool.AutoTool;
import me.pollos.polloshook.impl.module.player.avoid.Avoid;
import me.pollos.polloshook.impl.module.player.blink.Blink;
import me.pollos.polloshook.impl.module.player.choruscontrol.ChorusControl;
import me.pollos.polloshook.impl.module.player.fakeplayer.FakePlayer;
import me.pollos.polloshook.impl.module.player.fastbreak.FastBreak;
import me.pollos.polloshook.impl.module.player.fastplace.FastPlace;
import me.pollos.polloshook.impl.module.player.liquidinteract.LiquidInteract;
import me.pollos.polloshook.impl.module.player.mutlitask.MultiTask;
import me.pollos.polloshook.impl.module.player.nointerp.NoInterpolation;
import me.pollos.polloshook.impl.module.player.norotate.NoRotate;
import me.pollos.polloshook.impl.module.player.reach.Reach;
import me.pollos.polloshook.impl.module.player.rocketextend.FireworkExtend;
import me.pollos.polloshook.impl.module.player.scaffold.Scaffold;
import me.pollos.polloshook.impl.module.player.sprint.Sprint;
import me.pollos.polloshook.impl.module.player.suicide.Suicide;
import me.pollos.polloshook.impl.module.player.xcarry.XCarry;
import me.pollos.polloshook.impl.module.player.yaw.YawLock;
import me.pollos.polloshook.impl.module.render.betterchat.BetterChat;
import me.pollos.polloshook.impl.module.render.blockhighlight.BlockHighlight;
import me.pollos.polloshook.impl.module.render.breadcrumbs.BreadCrumbs;
import me.pollos.polloshook.impl.module.render.chams.Chams;
import me.pollos.polloshook.impl.module.render.crosshair.Crosshair;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import me.pollos.polloshook.impl.module.render.esp.ESP;
import me.pollos.polloshook.impl.module.render.extratab.ExtraTab;
import me.pollos.polloshook.impl.module.render.fovmodifier.FOVModifier;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import me.pollos.polloshook.impl.module.render.fullbright.Fullbright;
import me.pollos.polloshook.impl.module.render.glintmodify.GlintModify;
import me.pollos.polloshook.impl.module.render.holeesp.HoleESP;
import me.pollos.polloshook.impl.module.render.logoutspots.LogoutSpots;
import me.pollos.polloshook.impl.module.render.modelchanger.ModelChanger;
import me.pollos.polloshook.impl.module.render.nametags.Nametags;
import me.pollos.polloshook.impl.module.render.newchunks.NewChunks;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import me.pollos.polloshook.impl.module.render.noweather.NoWeather;
import me.pollos.polloshook.impl.module.render.oldpotions.OldPotions;
import me.pollos.polloshook.impl.module.render.shader.Shader;
import me.pollos.polloshook.impl.module.render.shulkerpreview.ShulkerPreview;
import me.pollos.polloshook.impl.module.render.skeleton.Skeleton;
import me.pollos.polloshook.impl.module.render.storageesp.StorageESP;
import me.pollos.polloshook.impl.module.render.tracers.Tracers;
import me.pollos.polloshook.impl.module.render.trajectories.Trajectories;
import me.pollos.polloshook.impl.module.render.viewclip.ViewClip;
import me.pollos.polloshook.impl.module.render.worldeditesp.WorldEditESP;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModuleManager extends SubscriberImpl implements Minecraftable, Initializable {
   private final Map<Class<? extends Module>, Module> modules = new LinkedHashMap();
   private final Map<Class<? extends HUDModule>, HUDModule> hudModules = new LinkedHashMap();
   private List<SimpleConfig> configurations = new ArrayList();
   private List<HudConfig> hudConfigs = new ArrayList();

   public ModuleManager() {
      this.listeners.add(new Listener<KeyPressEvent>(KeyPressEvent.class) {
         public void call(KeyPressEvent event) {
            Iterator var2 = ModuleManager.this.getModules().iterator();

            while (true) {
               while (true) {
                  ToggleableModule toggleable;
                  do {
                     Module module;
                     do {
                        if (!var2.hasNext()) {
                           return;
                        }

                        module = (Module) var2.next();
                     } while (!(module instanceof ToggleableModule));

                     toggleable = (ToggleableModule) module;
                  } while (event.getKey() != toggleable.getKeybind().getKey());

                  if (event.getAction() == KeyPressAction.PRESS) {
                     if (event.getKey() > 0 && mc.currentScreen == null) {
                        toggleable.getKeybind().onKeyPress();
                        ModuleManager.this.sendToggleMessage(toggleable);
                     }
                  } else if (event.getAction() == KeyPressAction.RELEASE || mc.currentScreen != null) {
                     Keybind var6 = toggleable.getKeybind();
                     if (var6 instanceof HoldKeybind) {
                        HoldKeybind hold = (HoldKeybind) var6;
                        hold.onKeyRelease();
                        if (mc.currentScreen == null) {
                           ModuleManager.this.sendToggleMessage(toggleable);
                        }
                     }
                  }
               }
            }
         }
      });
      this.listeners.add(new Listener<MouseClickEvent>(MouseClickEvent.class) {
         public void call(MouseClickEvent event) {
            if (event.getKey() != MouseButton.LEFT && event.getKey() != MouseButton.UNKNOWN
                  && mc.currentScreen == null) {
               Iterator var2 = ModuleManager.this.getModules().iterator();

               while (var2.hasNext()) {
                  Module module = (Module) var2.next();
                  if (module instanceof ToggleableModule) {
                     ToggleableModule toggleable = (ToggleableModule) module;
                     if (toggleable.getKeybind().getKey() == MouseUtil.reversed(event.getKey())
                           && event.getAction() == MouseClickAction.PRESS) {
                        toggleable.getKeybind().onKeyPress();
                        ModuleManager.this.sendToggleMessage(toggleable);
                     }
                  }
               }
            }

         }
      });
   }

   public void init() {
      this.register(new Aura());
      this.register(new Criticals());
      this.register(new AntiRegear());
      this.register(new AutoArmour());
      this.register(new AutoFeetPlace());
      this.register(new AutoTrap());
      this.register(new HoleFill());
      this.register(new Replenish());
      this.register(new AutoCrystal());
      this.register(new AutoExp());
      this.register(new SelfFill());
      this.register(new AutoTotem());
      this.register(new Blocker());
      this.register(new FastBow());
      this.register(new Blocker());
      this.register(new AutoWeb());
      this.register(new AutoCrystal());
      this.register(new IDPredict());
      this.register(new SelfBlocker());
      this.register(new PollosResolver());
      this.register(new ProjectileManip());
      this.register(new AutoLog());
      this.register(new DeathCoordsLog());
      this.register(new MiddleClick());
      this.register(new PayloadSpoof());
      this.register(new PacketLogger());
      this.register(new PopCounter());
      this.register(new VisualRange());
      this.register(new Timer());
      this.register(new Spammer());
      this.register(new PvPInfo());
      this.register(new Announcer());
      this.register(new AutoReply());
      this.register(new ChatAppend());
      this.register(new Swing());
      this.register(new NoQuitDesync());
      this.register(new AutoRespawn());
      this.register(new AntiInteract());
      this.register(new AntiHitbox());
      this.register(new NameProtect());
      this.register(new NoBreakAnim());
      this.register(new AntiAFK());
      this.register(new PingSpoof());
      this.register(new NoSoundLag());
      this.register(new AutoReconnect());
      this.register(new AntiAim());
      this.register(new Anchor());
      this.register(new Velocity());
      this.register(new NoAccel());
      this.register(new ElytraFly());
      this.register(new TickShift());
      this.register(new InvWalk());
      this.register(new Speed());
      this.register(new NoSlow());
      this.register(new Step());
      this.register(new ReverseStep());
      this.register(new HoleSnap());
      this.register(new Phase());
      this.register(new LiquidSpeed());
      this.register(new NoFall());
      this.register(new Fly());
      this.register(new AutoWalk());
      this.register(new TridentFly());
      this.register(new Jesus());
      this.register(new LongJump());
      this.register(new IceSpeed());
      this.register(new EntityControl());
      this.register(new EntitySpeed());
      this.register(new BoatFly());
      this.register(new ClickGUI());
      this.register(new HUD());
      this.register(new Colours());
      this.register(new Manager());
      this.register(new FastLatency());
      this.register(new IrcModule());
      this.register(new ClickGUI());
      this.register(new CustomFont());
      this.register(new Capes());
      this.register(new RPC());
      if (PollosHook.getSystemStatus() == SystemStatus.SUITABLE) {
         this.register(new WindowModule());
      }

      this.register(new FastBreak());
      this.register(new FastPlace());
      this.register(new FastBreak());
      this.register(new AutoTool());
      this.register(new FakePlayer());
      this.register(new LiquidInteract());
      this.register(new AutoMine());
      this.register(new AirPlace());
      this.register(new Blink());
      this.register(new Sprint());
      this.register(new NoRotate());
      this.register(new NoInterpolation());
      this.register(new MultiTask());
      this.register(new Reach());
      this.register(new XCarry());
      this.register(new FireworkExtend());
      this.register(new YawLock());
      this.register(new Avoid());
      this.register(new Scaffold());
      this.register(new Suicide());
      this.register(new ChorusControl());
      this.register(new BetterChat());
      this.register(new BlockHighlight());
      this.register(new ESP());
      this.register(new Fullbright());
      this.register(new HoleESP());
      this.register(new Nametags());
      this.register(new NewChunks());
      this.register(new NoRender());
      this.register(new ModelChanger());
      this.register(new BreadCrumbs());
      this.register(new LogoutSpots());
      this.register(new Tracers());
      this.register(new ShulkerPreview());
      this.register(new FOVModifier());
      this.register(new Skeleton());
      this.register(new CustomSky());
      this.register(new Chams());
      this.register(new GlintModify());
      this.register(new Trajectories());
      this.register(new ViewClip());
      this.register(new ExtraTab());
      this.register(new Freecam());
      this.register(new OldPotions());
      this.register(new NoWeather());
      this.register(new StorageESP());
      this.register(new WorldEditESP());
      this.register(new Shader());
      this.register(new Crosshair());
      this.registerHUD(new Coords());
      this.registerHUD(new Armor());
      this.registerHUD(new Arraylist());
      this.registerHUD(new HotbarKeys());
      this.registerHUD(new Info());
      this.registerHUD(new LagNotify());
      this.registerHUD(new Totems());
      this.registerHUD(new Watermark());
      this.registerHUD(new Welcomer());
      this.registerHUD(new DotGod());
      this.registerHUD(new VelocityGraph());
      this.registerHUD(new ClientMessages());
      this.registerHUD(new Health());
      this.registerHUD(new PearlCooldown());
      this.registerHUD(new ArmorWarning());
      this.registerHUD(new Inventory());
      this.registerHUD(new TextRadar());
      this.getAllModules().forEach(Module::onLoad);
      Iterator var1 = this.modules.values().iterator();

      AbstractConfig var10001;
      while (var1.hasNext()) {
         final Module module = (Module) var1.next();
         if (!module.getValues().isEmpty()) {
            var10001 = new AbstractConfig(module.getLabel().toLowerCase() + ".json", PollosHook.MODULES) {
               public void save() {
                  try {
                     new JsonObject();
                     Path outputFile = Paths.get(this.getFile().getAbsolutePath());
                     if (!Files.exists(outputFile, new LinkOption[0])) {
                        Files.createFile(outputFile);
                     }

                     Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                     String json = gson.toJson(module.write());
                     BufferedWriter writer = new BufferedWriter(
                           new OutputStreamWriter(Files.newOutputStream(outputFile)));
                     writer.write(json);
                     writer.close();
                  } catch (Exception var6) {
                     var6.printStackTrace();
                  }

               }

               public void load() {
                  try {
                     Path modulePath = Paths.get(this.getFile().getAbsolutePath());
                     if (!Files.exists(modulePath, new LinkOption[0])) {
                        return;
                     }

                     ModuleManager.this.loadPath(modulePath, module);
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }

               }
            };
         }
      }

      var10001 = new AbstractConfig("module_configs.json") {
         public void save() {
            ModuleManager.this.configurations.clear();
            Iterator var1 = ModuleManager.this.modules.values().iterator();

            while (var1.hasNext()) {
               Module module = (Module) var1.next();
               if (module instanceof ToggleableModule) {
                  ToggleableModule toggleable = (ToggleableModule) module;
                  int bind = toggleable.getKeybind().getKey();
                  String fromKeyCode;
                  switch (bind) {
                     case 1:
                        fromKeyCode = "RightClick";
                        break;
                     case 2:
                        fromKeyCode = "MiddleClick";
                        break;
                     case 3:
                        fromKeyCode = "Thumb3";
                        break;
                     case 4:
                        fromKeyCode = "Thumb4";
                        break;
                     default:
                        fromKeyCode = InputUtil.fromKeyCode(toggleable.getKeybind().getKey(), 0).getTranslationKey();
                  }

                  boolean isScanCode0 = fromKeyCode.equalsIgnoreCase("scancode.0");
                  SimpleConfig configx = new SimpleConfig(module.getLabel(), toggleable.isEnabled(), module.isDrawn(),
                        isScanCode0 ? "None" : fromKeyCode, toggleable.getKeybind() instanceof HoldKeybind);
                  ModuleManager.this.configurations.add(configx);
               } else {
                  SimpleConfig config = new SimpleConfig(module.getLabel(), true, module.isDrawn(),
                        InputUtil.UNKNOWN_KEY.getTranslationKey(), false);
                  ModuleManager.this.configurations.add(config);
               }
            }

            try {
               FileWriter writer = new FileWriter(this.getFile());

               try {
                  writer.write(
                        (new GsonBuilder()).setPrettyPrinting().create().toJson(ModuleManager.this.configurations));
               } catch (Throwable var9) {
                  try {
                     writer.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }

                  throw var9;
               }

               writer.close();
            } catch (IOException var10) {
               this.getFile().delete();
            }

         }

         public void load() {
            if (!this.getFile().exists()) {
               Path outputFile = Paths.get(this.getFile().toURI());
               if (!Files.exists(outputFile, new LinkOption[0])) {
                  try {
                     Files.createFile(outputFile);
                  } catch (IOException var15) {
                     var15.printStackTrace();
                  }
               }

            } else {
               try {
                  FileReader inFile = new FileReader(this.getFile());
                  try {
                     java.lang.reflect.Type listType = new TypeToken<ArrayList<SimpleConfig>>() {
                     }.getType();
                     Gson gson = new GsonBuilder().setPrettyPrinting().create();


                     List<SimpleConfig> loadedConfigs = gson.fromJson(inFile, listType);

                     if (loadedConfigs != null) {
                         ModuleManager.this.configurations = loadedConfigs;
                     } else {
                         ModuleManager.this.configurations = new ArrayList<>();
                         ClientLogger.getLogger().warn("PollosHook: module_configs.json was empty or malformed, starting with empty configurations."); 
                     }
                  } catch (Throwable var18) {
                     try {
                        inFile.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }

                     throw var18;
                  } finally { 
                       try {
                           if (inFile != null) {
                               inFile.close();
                           }
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                  }


               } catch (Exception var19) {
                  ClientLogger.getLogger().error("Couldn't load module configurations: " + var19.getMessage());
                   if (ModuleManager.this.configurations == null) {
                       ModuleManager.this.configurations = new ArrayList<>();
                   }
               }

               Iterator var20 = ModuleManager.this.configurations.iterator();
               while (true) {
                  while (var20.hasNext()) {
                     SimpleConfig simpleConfig = (SimpleConfig) var20.next();
                     String label = simpleConfig.label();
                     Iterator var4 = ModuleManager.this.modules.values().iterator();

                     while (var4.hasNext()) {
                        Module module = (Module) var4.next();
                        if (module.getLabel().equalsIgnoreCase(label) && module instanceof ToggleableModule) {
                           ToggleableModule toggleableModule = (ToggleableModule) module;
                           String bind = simpleConfig.bind();
                           boolean enabled = simpleConfig.enabled();
                           boolean drawn = simpleConfig.drawn();
                           boolean hold = simpleConfig.hold();
                           boolean ignored = hold || label.equalsIgnoreCase("clickgui")
                                 ||
                                 label.equalsIgnoreCase("fakeplayer");
                           if (enabled && !ignored) {
                              toggleableModule.setEnabled(true);
                           }

                           if (drawn) {
                              module.setDrawn(true);
                           }

                           int code;
                           try {
                              byte var14 = -1;
                              switch (bind.hashCode()) {
                                 case -1790479043:
                                    if (bind.equals("Thumb3")) {
                                       var14 = 2;
                                    }
                                    break;
                                 case -1790479042:
                                    if (bind.equals("Thumb4")) {
                                       var14 = 3;
                                    }
                                    break;
                                 case 2433880:
                                    if (bind.equals("None")) {
                                       var14 = 4;
                                    }
                                    break;
                                 case 579723500:
                                    if (bind.equals("RightClick")) {
                                       var14 = 0;
                                    }
                                    break;
                                 case 2063030483:
                                    if (bind.equals("MiddleClick")) {
                                       var14 = 1;
                                    }
                              }

                              switch (var14) {
                                 case 0:
                                    code = 1;
                                    break;
                                 case 1:
                                    code = 2;
                                    break;
                                 case 2:
                                    code = 3;
                                    break;
                                 case 3:
                                    code = 4;
                                    break;
                                 case 4:
                                    code = -1;
                                    break;
                                 default:
                                    code = InputUtil.fromTranslationKey(bind).getCode();
                                }
                           } catch (Exception var16) {
                              code = -1;
                           }
                           /*
                           if (hold) {
                              HoldKeybind kbx = new HoldKeybind(code) {
                                 // $FF: synthetic field
                                 final ToggleableModule val$toggleableModule;
                                 {
                                    super(key);
                                    this.val$toggleableModule = var3;
                                 }

                                 public void onKeyPress() {
                                    this.val$toggleableModule.setEnabled(true);
                                 }

                                 public void onKeyHold() {
                                 }

                                 public void onKeyRelease() {
                                    this.val$toggleableModule.setEnabled(false);
                                 }
                              };
                              toggleableModule.setKeybind(kbx);
                           } else {
                              Keybind kb = new Keybind(this, code, toggleableModule) {
                                 // $FF: synthetic field
                                 final ToggleableModule val$toggleableModule;
                                 {
                                    super(key);
                                    this.val$toggleableModule = var3;
                                 }

                                 public void onKeyPress() {
                                    this.val$toggleableModule.toggle();
                                 }
                              };
                              toggleableModule.setKeybind(kb);
                           }
                           */

                           break;
                        }
                     }
                  }
                  return;
               }
            }
         }
      };
      var1 = this.hudModules.values().iterator();

      while (var1.hasNext()) {
         final HUDModule hudModule = (HUDModule) var1.next();
         if (!hudModule.getValues().isEmpty()) {
            var10001 = new AbstractConfig(hudModule.getLabel().toLowerCase() + ".json", PollosHook.ELEMENTS) {
               public void save() {
                  try {
                     new JsonObject();
                     Path outputFile = Paths.get(this.getFile().getAbsolutePath());
                     if (!Files.exists(outputFile, new LinkOption[0])) {
                        Files.createFile(outputFile);
                     }

                     Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                     String json = gson.toJson(hudModule.write());
                     BufferedWriter writer = new BufferedWriter(
                           new OutputStreamWriter(Files.newOutputStream(outputFile)));
                     writer.write(json);
                     writer.close();
                  } catch (Exception var6) {
                     var6.printStackTrace();
                  }

               }

               public void load() {
                  try {
                     Path modulePath = Paths.get(this.getFile().getAbsolutePath());
                     if (!Files.exists(modulePath, new LinkOption[0])) {
                        return;
                     }

                     ModuleManager.this.loadPath(modulePath, hudModule);
                  } catch (Exception var2) {
                     var2.printStackTrace();
                  }

               }
            };
         }
      }

      var10001 = new AbstractConfig("hud_resolution.txt") {
         public void load() {
            try {
               if (!this.getFile().exists()) {
                  this.save();
                  return;
               }

               FileInputStream fstream = new FileInputStream(this.getFile().getAbsolutePath());

               try {
                  DataInputStream in = new DataInputStream(fstream);

                  try {
                     BufferedReader br = new BufferedReader(new InputStreamReader(in));

                     String line;
                     try {
                        while ((line = br.readLine()) != null) {
                           String[] parts = line.split(":");
                           if (parts.length == 2) {
                              try {
                                 DraggableHUDModule.INIT_WIDTH = Integer.parseInt(parts[1]);
                                 DraggableHUDModule.INIT_HEIGHT = Integer.parseInt(parts[0]);
                              } catch (NumberFormatException var10) {
                              }
                           }
                        }
                     } catch (Throwable var11) {
                        try {
                           br.close();
                        } catch (Throwable var9) {
                           var11.addSuppressed(var9);
                        }

                        throw var11;
                     }

                     br.close();
                  } catch (Throwable var12) {
                     try {
                        in.close();
                     } catch (Throwable var8) {
                        var12.addSuppressed(var8);
                     }

                     throw var12;
                  }

                  in.close();
               } catch (Throwable var13) {
                  try {
                     fstream.close();
                  } catch (Throwable var7) {
                     var13.addSuppressed(var7);
                  }

                  throw var13;
               }

               fstream.close();
            } catch (Exception var14) {
               this.save();
            }

         }

         public void save() {
            try {
               BufferedWriter out = new BufferedWriter(new FileWriter(this.getFile()));
               int var10001 = Minecraftable.mc.getWindow().getFramebufferHeight();
               out.write(var10001 + ":" + Minecraftable.mc.getWindow().getFramebufferWidth());
               out.write("\r\n");
               out.close();
            } catch (Exception var2) {
               var2.printStackTrace();
            }

         }
      };
      
      var10001 = new AbstractConfig("hud_configs.json") {
         public void save() {
            ModuleManager.this.hudConfigs.clear();
            Iterator var1 = ModuleManager.this.hudModules.values().iterator();

            while (var1.hasNext()) {
               HUDModule module = (HUDModule) var1.next();
               HudConfig config = new HudConfig(module);
               ModuleManager.this.hudConfigs.add(config);
            }

            try {
               FileWriter writer = new FileWriter(this.getFile());

               try {
                  writer.write((new GsonBuilder()).setPrettyPrinting().create().toJson(ModuleManager.this.hudConfigs));
               } catch (Throwable var5) {
                  try {
                     writer.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }

                  throw var5;
               }

               writer.close();
            } catch (IOException var6) {
               this.getFile().delete();
            }

         }

         public void load() {
            if (!this.getFile().exists()) {
               Path outputFile = Paths.get(this.getFile().toURI());
               if (!Files.exists(outputFile, new LinkOption[0])) {
                  try {
                     Files.createFile(outputFile);
                  } catch (IOException var13) {
                     var13.printStackTrace();
                  }
               }

            } else {
               FileReader inFile = null; 
               try {
                  inFile = new FileReader(this.getFile());
                  java.lang.reflect.Type listType = new TypeToken<ArrayList<HudConfig>>() {
                  }.getType();
                  Gson gson = new GsonBuilder().setPrettyPrinting().create();

                  List<HudConfig> loadedHudConfigs = gson.fromJson(inFile, listType);

                  if (loadedHudConfigs != null) {
                      ModuleManager.this.hudConfigs = loadedHudConfigs;
                  } else {
                      ModuleManager.this.hudConfigs = new ArrayList<>();
                      ClientLogger.getLogger().warn("PollosHook: hud_configs.json was empty or malformed, starting with empty HUD configurations."); 
                  }

               } catch (Exception var16) {
                  ClientLogger.getLogger().error("Couldn't load hud-module configurations: " + var16.getMessage());
                   if (ModuleManager.this.hudConfigs == null) {
                       ModuleManager.this.hudConfigs = new ArrayList<>();
                   }
               } finally {
                   try {
                       if (inFile != null) {
                           inFile.close();
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

               Iterator var17 = ModuleManager.this.hudConfigs.iterator();
               while (var17.hasNext()) {
                  HudConfig hudConfig = (HudConfig) var17.next();
                  String label = hudConfig.getLabel();
                  boolean enabled = hudConfig.isEnabled();
                  int x = hudConfig.getX();
                  int y = hudConfig.getY();
                  int w = hudConfig.getWidth();
                  int h = hudConfig.getHeight();
                  DraggableHUDModule.HudPosition p = hudConfig.getPart();
                  Iterator var10 = ModuleManager.this.hudModules.values().iterator();
                  while (var10.hasNext()) {
                     HUDModule module = (HUDModule) var10.next();
                     if (module.getLabel().equalsIgnoreCase(label)) {
                        if (enabled) {
                           module.setEnabled(true);
                        }

                        if (module instanceof DraggableHUDModule) {
                           DraggableHUDModule drag = (DraggableHUDModule) module;
                           drag.setTextX((float) x);
                           drag.setTextY((float) y);
                           drag.setTextWidth((float) w);
                           drag.setTextHeight((float) h);
                           drag.setPosition(p);
                        }
                     }
                  }
               }

            }
         }
      };
   }

   private void sendToggleMessage(ToggleableModule toggleable) {
      boolean enabled = toggleable.isEnabled();
      if ((Boolean) Manager.get().getForgeHax().getValue()) {
         ClientLogger.getLogger().logNoMark(String.valueOf(Formatting.GRAY)
               + "> %s.enabled = %s".formatted(new Object[] { toggleable.getLabel(), enabled }), 4444);
      } else {
         MutableText label = Text.literal(toggleable.getLabel()).styled((style) -> {
            return style.withBold(true);
         });
         Text was = Text.literal(" was ").styled((style) -> {
            return style.withColor(Manager.get().getThemeColor().getColor().getRGB()).withBold(false);
         });
         Formatting color = enabled ? Formatting.GREEN : Formatting.RED;
         Text on = Text.literal(enabled ? "enabled" : "disabled").styled((style) -> {
            return style.withFormatting(color).withBold(false);
         });
         label.formatted(Formatting.GRAY);
         MutableText mutableText = label.append(was).append(on);
         ClientLogger.getLogger().log((Text) mutableText, 4444);
      }

      ClientMessages CLIENT_MESSAGES = (ClientMessages) this.getHUD(ClientMessages.class);
      CLIENT_MESSAGES.displayModule("toggling module %s %s"
            .formatted(new Object[] { toggleable.getLabel().toLowerCase(), enabled ? "on" : "off" }));
   }

   public Collection<Module> getModules() {
      return this.modules.values();
   }

   public Collection<HUDModule> getHUDModules() {
      return this.hudModules.values();
   }

   public Collection<DraggableHUDModule> getDraggableHUDModules() {
      return (Collection) this.hudModules.values().stream().filter((module) -> {
         return module instanceof DraggableHUDModule;
      }).map((module) -> {
         return (DraggableHUDModule) module;
      }).collect(Collectors.toList());
   }

   public List<Module> getAllModules() {
      List<Module> mods = new ArrayList();
      mods.addAll(this.modules.values());
      mods.addAll(this.hudModules.values());
      return mods;
   }

   public List<Module> getModulesSorted() {
      List<Module> modulesList = new ArrayList(this.modules.values());
      modulesList.sort(Comparator.comparing(Module::getLabel));
      return modulesList;
   }

   public ModuleManager start(String startMessage) {
      this.info(startMessage);
      return this;
   }

   public ModuleManager finish(String finishMessage) {
      this.info(finishMessage);
      return this;
   }

   private void register(Module module) {
      this.modules.put(module.getClass(), module);
   }

   private void registerHUD(Module module) {
      HUDModule hudModule = (HUDModule) module;
      this.hudModules.put(hudModule.getClass(), hudModule);
   }

   public <T extends Module> T get(Class<T> clazz) {
      return (T) this.modules.get(clazz);
   }

   public <T extends HUDModule> T getHUD(Class<T> clazz) {
      return (T) this.hudModules.get(clazz);
   }

   public Module getModuleOrHUDByAlias(String alias) {
      Iterator var2 = this.modules.values().iterator();

      Module module;
      String[] var4;
      int var5;
      int var6;
      String aliases;
      while (var2.hasNext()) {
         module = (Module) var2.next();
         var4 = module.getAliases();
         var5 = var4.length;

         for (var6 = 0; var6 < var5; ++var6) {
            aliases = var4[var6];
            if (aliases.equalsIgnoreCase(alias)) {
               return module;
            }
         }
      }

      var2 = this.hudModules.values().iterator();

      while (var2.hasNext()) {
         module = (Module) var2.next();
         var4 = module.getAliases();
         var5 = var4.length;

         for (var6 = 0; var6 < var5; ++var6) {
            aliases = var4[var6];
            if (aliases.equalsIgnoreCase(alias)) {
               return module;
            }
         }
      }

      return null;
   }

   public Module getModuleByAlias(String alias) {
      Iterator var2 = this.modules.values().iterator();

      while (var2.hasNext()) {
         Module module = (Module) var2.next();
         String[] var4 = module.getAliases();
         int var5 = var4.length;

         for (int var6 = 0; var6 < var5; ++var6) {
            String aliases = var4[var6];
            if (aliases.equalsIgnoreCase(alias)) {
               return module;
            }
         }
      }

      return null;
   }

   private void loadPath(Path path, Module module) throws IOException {
      InputStream stream = Files.newInputStream(path);

      try {
         loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), module);
      } catch (IllegalStateException var5) {
         ClientLogger.getLogger()
               .error("Bad Config File for: %s [%s]".formatted(new Object[] { module.getLabel(), var5.getMessage() }));
         loadFile(new JsonObject(), module);
      }

      stream.close();
   }

   private static void loadFile(JsonObject input, Module module) {
      Iterator var2 = input.entrySet().iterator();

      while (var2.hasNext()) {
         Entry<String, JsonElement> entry = (Entry) var2.next();
         String propertyLabel = (String) entry.getKey();
         JsonElement element = (JsonElement) entry.getValue();
         Iterator var6 = module.getValues().iterator();

         while (var6.hasNext()) {
            Value<?> value = (Value) var6.next();
            if (propertyLabel.equals(value.getLabel())) {
               try {
                  module.load(value, element);
               } catch (Exception var9) {
                  ClientLogger.getLogger().error("Error loading module: %s [%s]"
                        .formatted(new Object[] { module.getLabel(), var9.getMessage() }));
               }
            }
         }
      }

   }
}
