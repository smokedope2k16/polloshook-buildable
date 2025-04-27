package me.pollos.polloshook.impl.manager.internal;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.command.core.Argument;
import me.pollos.polloshook.api.command.core.Command;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.SubscriberImpl;
import me.pollos.polloshook.api.interfaces.Initializable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.CommandModule;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.preset.Preset;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.KeybindValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.impl.ListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import me.pollos.polloshook.impl.command.friend.AddCommand;
import me.pollos.polloshook.impl.command.friend.FriendCommand;
import me.pollos.polloshook.impl.command.friend.RemoveCommand;
import me.pollos.polloshook.impl.command.irc.ClearPingsCommand;
import me.pollos.polloshook.impl.command.irc.FetchUsersCommand;
import me.pollos.polloshook.impl.command.irc.OnlineCommand;
import me.pollos.polloshook.impl.command.irc.PingCommand;
import me.pollos.polloshook.impl.command.macro.DualMacroCommand;
import me.pollos.polloshook.impl.command.macro.MacroCommand;
import me.pollos.polloshook.impl.command.modules.DrawnCommand;
import me.pollos.polloshook.impl.command.modules.DrawnMultipleCommand;
import me.pollos.polloshook.impl.command.modules.HoldBindCommand;
import me.pollos.polloshook.impl.command.modules.PanicCommand;
import me.pollos.polloshook.impl.command.modules.PresetCommand;
import me.pollos.polloshook.impl.command.modules.RenameCommand;
import me.pollos.polloshook.impl.command.modules.ResetCommand;
import me.pollos.polloshook.impl.command.modules.SetKeyBindCommand;
import me.pollos.polloshook.impl.command.modules.ToggleCommand;
import me.pollos.polloshook.impl.command.modules.configs.ConfigCommand;
import me.pollos.polloshook.impl.command.modules.configs.LoadCommand;
import me.pollos.polloshook.impl.command.modules.configs.SaveCommand;
import me.pollos.polloshook.impl.command.player.ChestSwapCommand;
import me.pollos.polloshook.impl.command.player.FireworkCommand;
import me.pollos.polloshook.impl.command.player.HClipCommand;
import me.pollos.polloshook.impl.command.player.HitboxDesyncCommand;
import me.pollos.polloshook.impl.command.player.LogTestCommand;
import me.pollos.polloshook.impl.command.player.VClipCommand;
import me.pollos.polloshook.impl.command.player.YawCommand;
import me.pollos.polloshook.impl.command.util.ClientWorldCommand;
import me.pollos.polloshook.impl.command.util.CoordsCommand;
import me.pollos.polloshook.impl.command.util.CrashCommand;
import me.pollos.polloshook.impl.command.util.ExecuteInCommand;
import me.pollos.polloshook.impl.command.util.FontsCommand;
import me.pollos.polloshook.impl.command.util.HelpCommand;
import me.pollos.polloshook.impl.command.util.LagbackCommand;
import me.pollos.polloshook.impl.command.util.OpenFileCommand;
import me.pollos.polloshook.impl.command.util.OpenFolderCommand;
import me.pollos.polloshook.impl.command.util.PrefixCommand;
import me.pollos.polloshook.impl.command.util.ReloadWorldCommand;
import me.pollos.polloshook.impl.command.util.SessionCommand;
import me.pollos.polloshook.impl.command.util.TutorialStepCommand;
import me.pollos.polloshook.impl.config.base.AbstractConfig;
import me.pollos.polloshook.impl.config.modules.ModuleConfig;
import me.pollos.polloshook.impl.events.network.PacketEvent;
import me.pollos.polloshook.impl.module.player.fakeplayer.utils.FakePlayerUtil;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Formatting;

public class CommandManager extends SubscriberImpl implements Initializable {
   private String prefix = ",";
   private final List<Command> commands = new ArrayList();
   private final Map<Command, Module> commandModules = new HashMap();

   public CommandManager() {
      this.listeners.add(new Listener<PacketEvent.Send<ChatMessageC2SPacket>>(PacketEvent.Send.class, Integer.MIN_VALUE, ChatMessageC2SPacket.class) {
         public void call(PacketEvent.Send<ChatMessageC2SPacket> event) {
            String message = ((ChatMessageC2SPacket)event.getPacket()).chatMessage();
            if (message.startsWith(CommandManager.this.getPrefix())) {
               boolean factoid = true;
               event.setCanceled(factoid);
               boolean exists = false;
               String[] args = message.split(" ");
               if (message.length() < 2) {
                  ClientLogger.getLogger().log("No command was entered");
                  return;
               }

               String input = message.split(" ")[0].substring(1);
               Iterator<Command> commandIterator = CommandManager.this.getCommands().iterator();

               while(commandIterator.hasNext()) {
                  Command command = commandIterator.next();
                  String[] aliases = command.getAliases();
                  int aliasCount = aliases.length;

                  for(int aliasIndex = 0; aliasIndex < aliasCount; ++aliasIndex) {
                     String alias = aliases[aliasIndex];
                     if (input.replace(CommandManager.this.getPrefix(), "").equalsIgnoreCase(alias.replaceAll(" ", ""))) {
                        exists = true;
                        CommandManager.this.executeSafe(command, args);
                     }
                  }
               }

               Iterator<Module> moduleIterator = Managers.getModuleManager().getAllModules().iterator();

               while(true) {
                  while(moduleIterator.hasNext()) {
                     Module mod = moduleIterator.next();
                     if (mod instanceof CommandModule) {
                        CommandModule commandModule = (CommandModule)mod;
                        if (commandModule.matching2Args(args)) {
                           CommandManager.this.executeSafe(commandModule.getCommand(), args);
                           exists = true;
                           continue;
                        }
                     }

                     String[] aliases = mod.getAliases();
                     int aliasCount = aliases.length;

                     for(int aliasIndex = 0; aliasIndex < aliasCount; ++aliasIndex) {
                        String alias = aliases[aliasIndex];

                        try {
                           if (args[0].equalsIgnoreCase(CommandManager.this.getPrefix() + alias.replace(" ", ""))) {
                              exists = true;
                              if (args.length <= 1) {
                                 ClientLogger.getLogger().log(String.format("%s [list|value] [list|get]", args[0]));
                              } else {
                                 String valueName = args[1];
                                 if (!args[1].equalsIgnoreCase("list")) {
                                    if (args[1].equalsIgnoreCase("export")) {
                                       JsonObject jsonObj = ModuleConfig.toJsonObject(mod);
                                       mc.keyboard.setClipboard(jsonObj.toString());
                                       ClientLogger.getLogger().log("Exported module %s to clipboard".formatted(new Object[]{mod.getLabel()}));
                                    } else if (args[1].equalsIgnoreCase("import")) {
                                       String clipboardContent = mc.keyboard.getClipboard();
                                       JsonObject jsonObj = JsonParser.parseString(clipboardContent).getAsJsonObject();
                                       ModuleConfig.loadValuesFromJson(jsonObj);
                                       ClientLogger.getLogger().log("Imported module %s from clipboard".formatted(new Object[]{mod.getLabel()}));
                                    } else if (args[1].equalsIgnoreCase("preset")) {
                                       if (args.length < 3) {
                                           ClientLogger.getLogger().log("Usage: " + args[0] + " preset <preset_name>");
                                           continue;
                                       }
                                       Preset preset = mod.getPresetByLabel(args[2]);
                                       if (preset != null) {
                                          preset.execute();
                                          ClientLogger.getLogger().log("Executed preset %s".formatted(new Object[]{preset.getLabel()}));
                                       } else {
                                          ClientLogger.getLogger().log("No preset with label %s".formatted(new Object[]{args[1]}));
                                       }
                                    } else {
                                       Value<?> val = mod.getValueByLabel(valueName);
                                       if (val == null) {
                                            ClientLogger.getLogger().log("Value '" + valueName + "' not found for module " + mod.getLabel());
                                            continue;
                                       }
                                       if (val.getValue() instanceof Boolean) {
                                          ClientLogger.getLogger().log(CommandManager.this.handleBoolean((Value<Boolean>)val));
                                        } else if (val instanceof TargetValue) {
                                          TargetValue targetValue = (TargetValue)val;
                                          ClientLogger.getLogger().log(targetValue.returnValue(args));
                                        } else if (val instanceof NumberValue) {
                                            NumberValue<?> numberValue = (NumberValue)val;
                                            ClientLogger.getLogger().log(numberValue.returnValue(args));
                                        } else if (val instanceof EnumValue) {
                                            EnumValue<?> enumValue = (EnumValue)val;
                                            ClientLogger.getLogger().log(enumValue.returnValue(args));
                                        } else if (val instanceof StringValue) {
                                            StringValue stringValue = (StringValue)val;
                                            ClientLogger.getLogger().log(stringValue.returnValue(args));
                                        } else if (val instanceof BlockListValue) {
                                            BlockListValue blockListValue = (BlockListValue)val;
                                            ClientLogger.getLogger().log(blockListValue.returnValue(args));
                                        } else if (val instanceof ItemListValue) {
                                            ItemListValue itemListValue = (ItemListValue)val;
                                            ClientLogger.getLogger().log(itemListValue.returnValue(args));
                                        } else if (val instanceof ColorValue) {
                                            ColorValue colorValue = (ColorValue)val;
                                            ClientLogger.getLogger().log(colorValue.returnValue(args));
                                        } else {
                                          ClientLogger.getLogger().log("That value does not exist");
                                        }
                                    }
                                 } else if (mod.getValues().isEmpty()) {
                                    ClientLogger.getLogger().log(String.format("%s%s%s has no values", Formatting.AQUA, mod.getLabel(), Formatting.RESET));
                                 } else {
                                    StringJoiner stringJoiner = new StringJoiner(", ");
                                    Iterator<Value<?>> valueIterator = mod.getValues().iterator();

                                    while(valueIterator.hasNext()) {
                                       Value<?> value = valueIterator.next();
                                       if (value instanceof TargetValue) {
                                          TargetValue targetValue = (TargetValue)value;
                                           String str = String.format("%s (Players %s | Bosses %s | Monsters %s | Friendlies %s) (IgnoreInvis %s | IgnoreNakeds %s | Targeting %s)",
                                              targetValue.getLabel(),
                                              targetValue.isTargetPlayers(),
                                              targetValue.isTargetMonsters(),
                                              targetValue.isTargetFriendlies(),
                                              targetValue.isIgnoreInvis(),
                                              targetValue.isIgnoreNaked(),
                                              targetValue.getTarget().name().toUpperCase());
                                          stringJoiner.add(str);
                                       } else if (value instanceof ColorValue) {
                                          ColorValue colorValue = (ColorValue)value;
                                          boolean isGlobal = colorValue.isGlobal();
                                          if (!isGlobal) {
                                             stringJoiner.add(String.format("%s, %s(Red %s, Green %s, Blue %s, Alpha %s)%s", colorValue.getLabel(), Formatting.GREEN, colorValue.getColor().getRed(), colorValue.getColor().getGreen(), colorValue.getColor().getBlue(), colorValue.getColor().getAlpha(), Formatting.RESET));
                                          } else {
                                             stringJoiner.add(String.format("%s [Global]", colorValue.getLabel()));
                                          }
                                       } else if (value instanceof KeybindValue) {
                                          KeybindValue bindValue = (KeybindValue)value;
                                          stringJoiner.add(String.format("Keybind [%s]", KeyboardUtil.getKeyNameFromNumber(((Keybind)bindValue.getValue()).getKey(), true)));
                                       } else if (value instanceof ListValue) {
                                          ListValue<?> listValue = (ListValue)value;
                                          stringJoiner.add(listValue.getLabel() + " (" + String.valueOf(((List<?>)listValue.getValue()).isEmpty() ? "Empty" : ((List<?>)listValue.getValue()).size()) + ")");
                                       } else {
                                          stringJoiner.add(String.format("%s [%s]", value.getLabel(), value.getValue() instanceof Enum ? ((EnumValue<?>)value).getFixedValue() : value.getValue()));
                                       }
                                    }

                                    ClientLogger.getLogger().log(String.format("Values (%s) %s", mod.getValues().size(), stringJoiner));
                                 }
                              }
                           }
                        } catch (Exception e) {
                           ClientLogger.getLogger().log(e.getMessage());
                        }
                     }
                  }

                  if (!exists) {
                     StringJoiner stringJoiner = new StringJoiner(", ");
                     Iterator<Command> unknownCommandIterator = CommandManager.this.getCommands().iterator();

                     while(unknownCommandIterator.hasNext()) {
                        Command command = unknownCommandIterator.next();
                        String args0 = args[0].replace(CommandManager.this.getPrefix(), "").toLowerCase();
                        if (command.getLabel().toLowerCase().startsWith(args0)) {
                           stringJoiner.add(command.getLabel());
                        }
                     }

                     ClientLogger.getLogger().log(String.format("Unknown command%s", stringJoiner.length() > 0 ? ", try: " + stringJoiner : ""));
                  }
                  break;
               }
            }

         }
      });
      this.register(new PrefixCommand());
      this.register(new HelpCommand());
      this.register(new ToggleCommand());
      this.register(new SetKeyBindCommand());
      this.register(new DrawnCommand());
      this.register(new DrawnMultipleCommand());
      this.register(new RenameCommand());
      this.register(new PresetCommand());
      this.register(new HoldBindCommand());
      this.register(new ResetCommand());
      this.register(new PanicCommand());
      this.register(new ChestSwapCommand());
      this.register(new FireworkCommand());
      this.register(new HitboxDesyncCommand());
      this.register(new HClipCommand());
      this.register(new VClipCommand());
      this.register(new YawCommand());
      this.register(new CoordsCommand());
      this.register(new OpenFolderCommand());
      this.register(new CrashCommand());
      this.register(new ClientWorldCommand());
      this.register(new SessionCommand());
      this.register(new LagbackCommand());
      this.register(new OpenFileCommand());
      this.register(new FontsCommand());
      this.register(new ReloadWorldCommand());
      this.register(new ExecuteInCommand());
      this.register(new TutorialStepCommand());
      if (PollosHook.isRunClient()) {
         this.register(new LogTestCommand());
      }

      if (PollosHook.isRunClient()) {
         this.register(new Command(new String[]{"addplayer", "addplayeretst"}, new Argument[0]) {
            public String execute(String[] args) {
               FakePlayerUtil.addFakePlayerToWorld("hahaha", "hahahaha", -12498214);
               return "done";
            }
         });
      }

      this.register(new LoadCommand());
      this.register(new SaveCommand());
      this.register(new ConfigCommand());
      this.register(new FriendCommand());
      this.register(new AddCommand());
      this.register(new RemoveCommand());
      this.register(new MacroCommand());
      this.register(new DualMacroCommand());
      this.register(new OnlineCommand());
      this.register(new PingCommand());
      this.register(new ClearPingsCommand());
      this.register(new FetchUsersCommand());
   }

   public void init() {
      AbstractConfig prefixConfig = new AbstractConfig("prefix.txt") {
         public void save() {
            try {
               BufferedWriter out = new BufferedWriter(new FileWriter(this.getFile()));
               out.write(CommandManager.this.getPrefix());
               out.write("\r\n");
               out.close();
            } catch (Exception e) {
               e.printStackTrace();
            }

         }

         public void load() {
            try {
               if (!this.getFile().exists()) {
                  this.save();
               }

               FileInputStream fstream = new FileInputStream(this.getFile().getAbsolutePath());
               DataInputStream in = new DataInputStream(fstream);
               BufferedReader br = new BufferedReader(new InputStreamReader(in));

               String line;
               while((line = br.readLine()) != null) {
                  CommandManager.this.setPrefix(line);
               }

               br.close();
            } catch (Exception e) {
               e.printStackTrace();
               this.save();
            }

         }
      };
       prefixConfig.load();
   }

   public CommandManager start(String startMessage) {
      this.info(startMessage);
      return this;
   }

   public CommandManager finish(String finishMessage) {
      this.info(finishMessage);
      return this;
   }

   private void executeSafe(Command command, String[] args) {
      try {
         ClientLogger.getLogger().log(command.execute(args));
      } catch (Exception e) {
         e.printStackTrace();
         ClientLogger.getLogger().log(Formatting.RED + command.getInfo());
      }

   }

   public void register(Command command) {
      this.commands.add(command);
   }

   public void registerModule(Command command, Module module) {
      this.commandModules.put(command, module);
   }

   public Collection<Command> collectCommands() {
      List<Command> allCommands = new ArrayList(this.commands);
      allCommands.addAll(this.commandModules.keySet());
      return allCommands;
   }

   public List<Command> getCommands() {
      return this.collectCommands().stream().toList();
   }

   public Module getModule(String[] args) {
      return Managers.getModuleManager().getModuleByAlias(args[0].replace(this.prefix, ""));
   }

   public static String currentMessage(Module module, Value<?> val, Object value) {
      return Formatting.AQUA + module.getLabel() + Formatting.RESET + " value " + Formatting.YELLOW + val.getLabel() + Formatting.RESET + " current value is " + Formatting.GREEN + String.valueOf(value);
   }

   public static String setColorMessage(Module module, Value<?> val, String color, Object value) {
      return Formatting.AQUA + module.getLabel() + Formatting.RESET + " value " + Formatting.YELLOW + val.getLabel() + Formatting.RESET + " " + color + " was set to " + Formatting.GREEN + String.valueOf(value);
   }

   public static String setMessage(Module module, Value<?> val, Object value) {
      return Formatting.AQUA + module.getLabel() + Formatting.RESET + " value " + Formatting.YELLOW + val.getLabel() + Formatting.RESET + " was set to " + Formatting.GREEN + String.valueOf(value);
   }

   private String handleBoolean(Value<Boolean> value) {
      value.setValue(!(Boolean)value.getValue());
      return String.format("%s%s%s value %s%s%s was %s",
          Formatting.AQUA, value.getLabel(), Formatting.RESET,
          Formatting.YELLOW, value.getLabel(), Formatting.RESET,
          (Boolean)value.getValue() ? Formatting.GREEN + "enabled" : Formatting.RED + "disabled");
   }

   
   public String getPrefix() {
      return this.prefix;
   }

   
   public Map<Command, Module> getCommandModules() {
      return this.commandModules;
   }

   
   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}