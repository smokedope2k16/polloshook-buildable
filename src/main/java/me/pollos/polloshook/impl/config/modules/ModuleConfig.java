package me.pollos.polloshook.impl.config.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.interfaces.Configurable;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Category;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.module.ToggleableModule;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.thread.FileUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.KeybindValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.StringValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumUtil;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.block.ToggleableBlock;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ToggleableItem;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import me.pollos.polloshook.impl.module.player.fakeplayer.FakePlayer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ModuleConfig implements Labeled, Configurable {
   private final String label;
   private File file;
   private LocalDateTime lastLoaded;
   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

   public ModuleConfig(String label) {
      this.label = label;
      this.file = new File(PollosHook.CONFIGS, label);
      Managers.getConfigManager().getRegistry().add(this);
   }

   public void load() {
      PollosHook.setCurrentConfig(this);
      ClientLogger.getLogger().info("Loading config: " + this.label);

      try {
         FileReader reader = FileUtil.createReader(this.file);

         JsonArray array;
         try {
            array = (JsonArray)JsonParser.parseReader(reader);
         } catch (ClassCastException var6) {
            this.save();
            return;
         }

         JsonArray modules = null;

         try {
            JsonObject modulesObject = (JsonObject)array.get(3);
            modules = modulesObject.getAsJsonArray("modules");
         } catch (Exception var5) {
            ClientLogger.getLogger().error("Skipping module array");
         }

         if (modules == null) {
            return;
         }

         modules.forEach((mod) -> {
            try {
               loadValuesFromJson(mod.getAsJsonObject());
            } catch (Exception var3) {
               Module errorModule = moduleFromJsonObject(mod.getAsJsonObject());
               ClientLogger var10000 = ClientLogger.getLogger();
               String var10001 = errorModule == null ? "null" : errorModule.getLabel();
               var10000.error("Failed to load values: " + var10001 + ", error: " + var3.getMessage());
            }

         });
      } catch (JsonIOException var7) {
         ClientLogger var10000 = ClientLogger.getLogger();
         String var10001 = this.label;
         var10000.error("Failed to load config " + var10001 + ": " + var7.getMessage());
      }

   }

   public void save() {
      ClientLogger.getLogger().info("Saving config: " + this.label);

      try {
         JsonObject dateHolder = new JsonObject();
         JsonObject infoObj = new JsonObject();
         LocalDateTime now = LocalDateTime.now();
         JsonArray array;
         if (this.file.exists()) {
            try {
               FileReader reader = new FileReader(this.file);

               try {
                  JsonElement jsonElement = JsonParser.parseReader(reader);
                  if (jsonElement.isJsonArray()) {
                     array = jsonElement.getAsJsonArray();
                     if (!array.isEmpty() && array.get(0).isJsonObject()) {
                        dateHolder = array.get(0).getAsJsonObject();
                        if (dateHolder.has("creation-info")) {
                           infoObj = dateHolder.getAsJsonObject("creation-info");
                        }
                     }
                  }
               } catch (Throwable var16) {
                  try {
                     reader.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }

                  throw var16;
               }

               reader.close();
            } catch (IOException var17) {
               var17.printStackTrace();
            }
         } else {
            ClientLogger.getLogger().info(this.file.createNewFile() ? "Successfully created config file" : "Failed to create config file");
         }

         if (!infoObj.has("author")) {
            infoObj.addProperty("author", MinecraftClient.getInstance().getSession().getUsername());
            infoObj.addProperty("time", this.formatter.format(now));
         }

         infoObj.addProperty("last-saved-at", this.formatter.format(now));
         infoObj.addProperty("last-saved-by", MinecraftClient.getInstance().getSession().getUsername());
         infoObj.addProperty("last-used", this.lastLoaded == null ? "Unknown" : this.formatter.format(this.lastLoaded));
         dateHolder.add("creation-info", infoObj);
         array = new JsonArray();
         array.add(dateHolder);
         JsonObject clientHolder = new JsonObject();
         JsonObject clientObj = new JsonObject();
         clientObj.addProperty("name", "polloshook");
         clientObj.addProperty("version", "v2.8.5");
         clientHolder.add("client-info", clientObj);
         array.add(clientHolder);
         JsonObject gameHolder = new JsonObject();
         JsonObject gameObj = new JsonObject();
         gameObj.addProperty("name", MinecraftClient.getInstance().getName());
         gameObj.addProperty("version", MinecraftClient.getInstance().getGameVersion());
         gameObj.addProperty("type", MinecraftClient.getInstance().getVersionType());
         gameHolder.add("game-info", gameObj);
         array.add(gameHolder);
         JsonObject modulesObj = new JsonObject();
         modulesObj.add("modules", getModuleArray());
         array.add(modulesObj);
         FileWriter writer = new FileWriter(this.file);

         try {
            Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
            gson.toJson(array, writer);
         } catch (Throwable var14) {
            try {
               writer.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }

            throw var14;
         }

         writer.close();
      } catch (IOException var18) {
         var18.printStackTrace();
      }

   }

   public static JsonArray getModuleArray() {
      JsonArray modulesArray = new JsonArray();
      Iterator var1 = Managers.getModuleManager().getModules().iterator();

      while(var1.hasNext()) {
         Module module = (Module)var1.next();
         if (module.getCategory() != Category.OTHER && !module.getLabel().equalsIgnoreCase("fakeplayer")) {
            JsonObject jsonObject = toJsonObject(module);
            modulesArray.add(jsonObject);
         }
      }

      return modulesArray;
   }

   public static JsonObject toJsonObject(Module module) {
      JsonObject attribs = new JsonObject();
      attribs.addProperty("Enabled", module instanceof ToggleableModule && ((ToggleableModule)module).isEnabled());
      if (!module.getValues().isEmpty()) {
         Iterator var2 = module.getValues().iterator();

         while(var2.hasNext()) {
            Value<?> value = (Value)var2.next();
            if (value.getValue() instanceof Number) {
               attribs.addProperty(value.getLabel(), (Number)value.getValue());
            } else if (value.getValue() instanceof Boolean) {
               attribs.addProperty(value.getLabel(), (Boolean)value.getValue());
            } else if (value instanceof EnumValue) {
               attribs.addProperty(value.getLabel(), String.valueOf(((EnumValue)value).getFixedValue()));
            } else if (value instanceof StringValue) {
               StringValue stringValue = (StringValue)value;
               stringValue.setValue(value.getValue().toString().replace(" ", "_"));
               attribs.addProperty(value.getLabel(), (String)stringValue.getValue());
            } else if (value instanceof KeybindValue) {
               KeybindValue keybindValue = (KeybindValue)value;
               attribs.addProperty(value.getLabel(), InputUtil.fromKeyCode(((Keybind)keybindValue.getValue()).getKey(), 0).getCode());
            } else {
               JsonArray jsonArray;
               if (value instanceof ColorValue) {
                  ColorValue colorProperty = (ColorValue)value;
                  jsonArray = new JsonArray();
                  Color color = (Color)colorProperty.getValue();
                  String hex = ColorUtil.colorToHex(color);
                  jsonArray.add(hex);
                  jsonArray.add(JsonParser.parseString(String.valueOf(colorProperty.isGlobal())));
                  attribs.add(value.getLabel(), jsonArray);
               } else {
                  ArrayList list;
                  if (value instanceof BlockListValue) {
                     BlockListValue blockList = (BlockListValue)value;
                     jsonArray = new JsonArray();
                     list = (ArrayList)blockList.getValue();
                     list.forEach((block) -> {
                        String var10001 = ((Block)Objects.requireNonNull((Block)Registries.BLOCK.get(((ToggleableBlock) block).getBlockRegistryEntry()))).toString().replace("Block{", "").replace("}", "");
                        jsonArray.add(var10001 + "|" + ((ToggleableModule) block).isEnabled());
                     });
                     attribs.add(value.getLabel(), jsonArray);
                  } else if (value instanceof ItemListValue) {
                     ItemListValue itemList = (ItemListValue)value;
                     jsonArray = new JsonArray();
                     list = (ArrayList)itemList.getValue();
                     list.forEach((item) -> {
                        Item resourceLocation = (Item)Registries.ITEM.get(((ToggleableItem) item).getItemRegistryEntry());
                        if (resourceLocation != null) {
                           String var10001 = resourceLocation.toString().replace("Item{", "").replace("}", "");
                           jsonArray.add(var10001 + "|" + ((ToggleableModule) item).isEnabled());
                        }

                     });
                     attribs.add(value.getLabel(), jsonArray);
                  }
               }
            }
         }
      }

      JsonObject moduleObject = new JsonObject();
      moduleObject.add(module.getLabel(), attribs);
      return moduleObject;
   }

   public static void loadValuesFromJson(JsonObject object) throws NullPointerException {
      Module module = moduleFromJsonObject(object);
      if (module != null) {
         JsonObject moduleObject = object.getAsJsonObject(module.getLabel());
         if (!(module instanceof FakePlayer) && module instanceof ToggleableModule) {
            ToggleableModule toggleableModule = (ToggleableModule)module;
            boolean enabled = moduleObject.get("Enabled").getAsBoolean();
            toggleableModule.setEnabled(enabled);
         }

         if (!module.getValues().isEmpty()) {
            Iterator var21 = module.getValues().iterator();

            while(true) {
               while(true) {
                  Value value;
                  do {
                     if (!var21.hasNext()) {
                        return;
                     }

                     value = (Value)var21.next();
                  } while(value == null);

                  String valueLabel = value.getLabel();
                  if (value instanceof NumberValue) {
                     NumberValue numberValue = (NumberValue)value;

                     try {
                        if (numberValue.getValue() instanceof Long) {
                           long l = moduleObject.getAsJsonPrimitive(valueLabel).getAsLong();
                           numberValue.setValueNoLimit(l);
                        } else if (numberValue.getValue() instanceof Integer) {
                           int i = moduleObject.getAsJsonPrimitive(valueLabel).getAsInt();
                           numberValue.setValueNoLimit(i);
                        } else if (numberValue.getValue() instanceof Float) {
                           float f = moduleObject.getAsJsonPrimitive(valueLabel).getAsFloat();
                           numberValue.setValueNoLimit(f);
                        } else if (numberValue.getValue() instanceof Double) {
                           double d = moduleObject.getAsJsonPrimitive(valueLabel).getAsDouble();
                           numberValue.setValueNoLimit(d);
                        } else if (numberValue.getValue() instanceof Short) {
                           short s = moduleObject.getAsJsonPrimitive(valueLabel).getAsShort();
                           numberValue.setValueNoLimit(s);
                        } else if (numberValue.getValue() instanceof Byte) {
                           byte elementCodec = moduleObject.getAsJsonPrimitive(valueLabel).getAsByte();
                           numberValue.setValueNoLimit(elementCodec);
                        }
                     } catch (Exception var20) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value.getValue() instanceof Boolean) {
                     try {
                        boolean bl = moduleObject.getAsJsonPrimitive(valueLabel).getAsBoolean();
                        value.setValue(bl);
                     } catch (Exception var19) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value.getValue() instanceof Double) {
                     try {
                        value.setValue(moduleObject.getAsJsonPrimitive(valueLabel).getAsDouble());
                     } catch (Exception var18) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value.getValue() instanceof Float) {
                     try {
                        value.setValue(moduleObject.getAsJsonPrimitive(valueLabel).getAsFloat());
                     } catch (Exception var17) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value.getValue() instanceof String) {
                     try {
                        String str = moduleObject.getAsJsonPrimitive(valueLabel).getAsString();
                        value.setValue(str.replace("_", " "));
                     } catch (Exception var16) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value.getValue() instanceof KeybindValue) {
                     try {
                        value.setValue(InputUtil.fromTranslationKey(moduleObject.getAsJsonPrimitive(valueLabel).getAsString()));
                     } catch (Exception var15) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value instanceof EnumValue) {
                     try {
                        value.setValue(EnumUtil.fromString((Enum)value.getValue(), moduleObject.getAsJsonPrimitive(valueLabel).getAsString()));
                     } catch (Exception var14) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else if (value.getValue() instanceof Color && (Boolean)Manager.get().getLoadColorConfigs().getValue()) {
                     try {
                        JsonArray array = moduleObject.getAsJsonArray(valueLabel);
                        ColorValue colorProperty = (ColorValue)value;
                        String hex = array.get(0).getAsString();
                        boolean global = array.get(1).getAsBoolean();
                        colorProperty.setValue(ColorUtil.hexToColor(hex));
                        colorProperty.setGlobal(global);
                     } catch (Exception var13) {
                        ClientLogger.getLogger().warn("why");
                     }
                  } else {
                     ArrayList blocks;
                     JsonArray blockJsonArray;
                     if (value instanceof ItemListValue) {
                        try {
                           ItemListValue itemList = (ItemListValue)value;
                           blocks = new ArrayList();
                           blockJsonArray = moduleObject.getAsJsonArray(valueLabel);
                           blockJsonArray.forEach((elem) -> {
                              String[] split = elem.getAsString().split("\\|");
                              boolean toggled = Boolean.parseBoolean(split[1]);
                              Item item = (Item)Registries.ITEM.get(Identifier.of(split[0].replace("minecraft:", "")));
                              blocks.add(new ToggleableItem(item, toggled));
                           });
                           itemList.setValue(blocks);
                        } catch (Exception var12) {
                           ClientLogger.getLogger().warn("why");
                        }
                     } else if (value instanceof BlockListValue) {
                        try {
                           BlockListValue blockList = (BlockListValue)value;
                           blocks = new ArrayList();
                           blockJsonArray = moduleObject.getAsJsonArray(valueLabel);
                           blockJsonArray.forEach((elem) -> {
                              String[] split = elem.getAsString().split("\\|");
                              boolean toggled = Boolean.parseBoolean(split[1]);
                              Block block = (Block)Registries.BLOCK.get(Identifier.of(split[0].replace("minecraft:", "")));
                              blocks.add(new ToggleableBlock(block, toggled));
                           });
                           blockList.setValue(blocks);
                        } catch (Exception var11) {
                           ClientLogger.getLogger().warn("why");
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static Module moduleFromJsonObject(JsonObject object) {
      return (Module)Managers.getModuleManager().getModules().stream().filter((mod) -> {
         return object.getAsJsonObject(mod.getLabel()) != null;
      }).findFirst().orElse((Module)null);
   }

   public static List<File> getConfigList() {
      if (PollosHook.DIRECTORY.exists() && PollosHook.DIRECTORY.listFiles() != null) {
         return PollosHook.CONFIGS.listFiles() != null ? (List)Arrays.stream((File[])Objects.requireNonNull(PollosHook.CONFIGS.listFiles())).filter((f) -> {
            return f.getName().endsWith(".cfg");
         }).collect(Collectors.toList()) : null;
      } else {
         return null;
      }
   }

   public void loadTime() {
      FileReader reader = null;

      try {
         reader = new FileReader(this.file);
      } catch (FileNotFoundException var9) {
         ClientLogger.getLogger().error("Failed to initialize file reader" + var9.getMessage());
      }

      JsonArray array = null;

      try {
         array = (JsonArray)JsonParser.parseReader(reader);
      } catch (ClassCastException var8) {
         this.save();
      }

      if (array != null) {
         JsonObject dateHolder = null;
         Iterator var4 = array.iterator();

         while(var4.hasNext()) {
            JsonElement element = (JsonElement)var4.next();
            if (element.isJsonObject() && element.getAsJsonObject().has("creation-info")) {
               dateHolder = element.getAsJsonObject();
               break;
            }
         }

         if (dateHolder != null) {
            JsonObject dateObj = dateHolder.getAsJsonObject("creation-info");
            if (dateObj.has("last-used")) {
               String lastUsedStr = dateObj.get("last-used").getAsString();
               if (!lastUsedStr.equals("Unknown")) {
                  try {
                     this.lastLoaded = LocalDateTime.parse(lastUsedStr, this.formatter);
                  } catch (Exception var7) {
                     var7.printStackTrace();
                  }
               } else {
                  this.lastLoaded = null;
               }
            }
         }
      }

   }

   public static ArrayList<String> getConfigAsString() {
      List<ToggleableModule> toggleableModules = Managers.getModuleManager().getModules().stream().filter((m) -> {
         return m instanceof ToggleableModule;
      }).map((m) -> {
         return (ToggleableModule)m;
      }).toList();
      ArrayList<String> strings = new ArrayList();
      toggleableModules.forEach((t) -> {
         strings.add(toJsonObject(t).toString());
      });
      return strings;
   }

   public LocalDateTime getLastUsedFromFile() {
      FileReader reader = FileUtil.createReader(this.getFile());
      JsonArray array = null;

      try {
         array = (JsonArray)JsonParser.parseReader(reader);
      } catch (ClassCastException var7) {
         this.save();
      }

      try {
         JsonObject obj = array.get(0).getAsJsonObject();
         JsonObject creationInfo = obj.getAsJsonObject("creation-info");
         if (this.getLastLoaded() != null) {
            return this.getLastLoaded();
         } else {
            String timeString = creationInfo.get("last-used").getAsString();
            return LocalDateTime.parse(timeString, this.formatter);
         }
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }

   public void updateLastLoaded() {
      this.lastLoaded = LocalDateTime.now();
   }

   
   public String getLabel() {
      return this.label;
   }

   
   public File getFile() {
      return this.file;
   }

   
   public LocalDateTime getLastLoaded() {
      return this.lastLoaded;
   }

   
   public DateTimeFormatter getFormatter() {
      return this.formatter;
   }

   
   public void setFile(File file) {
      this.file = file;
   }
}
