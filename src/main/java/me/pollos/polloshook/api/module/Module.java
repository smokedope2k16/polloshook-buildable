package me.pollos.polloshook.api.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.event.bus.api.Subscriber;
import me.pollos.polloshook.api.interfaces.Labeled;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.preset.Preset;
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
import me.pollos.polloshook.api.value.value.targeting.TargetPreset;
import me.pollos.polloshook.api.value.value.targeting.TargetValue;
import net.minecraft.block.Block;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class Module implements Subscriber, Labeled, Minecraftable {
   private final String[] aliases;
   private final Category category;
   private final ArrayList<Value<?>> values = new ArrayList();
   private final ArrayList<Preset> presets = new ArrayList();
   private final List<Listener> listeners = new ArrayList();
   private String displayLabel;
   private boolean drawn = false;

   public Module(String[] aliases, Category category) {
      this.displayLabel = aliases[0];
      this.aliases = aliases;
      this.category = category;
      this.offerPresets(new Preset(new String[]{"Default", "def"}) {
         public void execute() {
            if (!Module.this.values.isEmpty()) {
               Module.this.values.forEach(Value::resetToDefaultValue);
            }
         }
      });
   }

   public void onLoad() {
   }

   public void onWorldLoad() {
   }

   public void onGameJoin() {
   }

   public void onShutdown() {
   }

   public String getLabel() {
      return this.aliases.length > 0 ? this.aliases[0] : "missing";
   }

   public Color getModuleColor() {
      int hash = Objects.hash(new Object[]{this.category, Arrays.hashCode(this.aliases)});
      int r = (hash & 16711680) >> 16;
      int g = (hash & '\uff00') >> 8;
      int b = hash & 255;
      return new Color(r, g, b);
   }

   protected String getTag() {
      return null;
   }

   public String getFullTag() {
      return this.getTag() == null ? "" : this.getTag();
   }

   protected void offerPresets(Preset... presets) {
      this.presets.addAll(Arrays.asList(presets));
   }

   protected void offerValues(Value<?>... values) {
      this.values.addAll(Arrays.asList(values));
   }

   protected void offerListeners(Listener<?>... listeners) {
      this.listeners.addAll(Arrays.asList(listeners));
   }

   public Value<?> getValueByLabel(String alias) {
      Iterator var2 = this.values.iterator();

      while(var2.hasNext()) {
         Value<?> property = (Value)var2.next();
         String[] var4 = property.getAliases();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String aliases = var4[var6];
            if (alias.equalsIgnoreCase(aliases)) {
               return property;
            }
         }
      }

      return null;
   }

   public Preset getPresetByLabel(String label) {
      Iterator var2 = this.presets.iterator();

      Preset preset;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         preset = (Preset)var2.next();
      } while(!label.equalsIgnoreCase(preset.getLabel()));

      return preset;
   }

   public JsonObject write() {
      JsonObject object = new JsonObject();
      JsonParser jp = new JsonParser();
      Iterator var3 = this.getValues().iterator();

      while(var3.hasNext()) {
         Value<?> val = (Value)var3.next();
         JsonArray jsonArray;
         if (val instanceof TargetValue) {
            TargetValue enemyFindingValue = (TargetValue)val;
            jsonArray = this.getJsonElements(enemyFindingValue);
            object.add(enemyFindingValue.getLabel(), jsonArray);
         } else if (val instanceof ColorValue) {
            ColorValue colorValue = (ColorValue)val;
            Color color = (Color)colorValue.getValue();
            JsonArray colorArray = new JsonArray();
            String hex = ColorUtil.colorToHex(color);
            colorArray.add(hex);
            colorArray.add(jp.parse(String.valueOf(colorValue.isGlobal())));
            object.add(val.getLabel(), colorArray);
         } else if (val instanceof StringValue) {
            StringValue stringValue = (StringValue)val;
            object.addProperty(stringValue.getLabel(), ((String)stringValue.getValue()).replace(" ", "_"));
         } else if (val instanceof ItemListValue) {
            ItemListValue itemList = (ItemListValue)val;
            jsonArray = this.getJsonElements(itemList);
            object.add(itemList.getLabel(), jsonArray);
         } else if (val instanceof BlockListValue) {
            BlockListValue blockList = (BlockListValue)val;
            jsonArray = this.getJsonElements(blockList);
            object.add(blockList.getLabel(), jsonArray);
         } else if (val instanceof KeybindValue) {
            KeybindValue keybindValue = (KeybindValue)val;
            if (!val.getLabel().equalsIgnoreCase("keybind")) {
               int bind = ((Keybind)keybindValue.getValue()).getKey();
               String fromKeyCode;
               switch(bind) {
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
                  fromKeyCode = InputUtil.fromKeyCode(bind, 0).getTranslationKey();
               }

               boolean isScanCode0 = fromKeyCode.equalsIgnoreCase("scancode.0");
               object.addProperty(keybindValue.getLabel(), isScanCode0 ? "None" : fromKeyCode);
            }
         } else {
            try {
               object.add(val.getLabel(), jp.parse(val.getValue().toString()));
            } catch (Exception var9) {
               ClientLogger.getLogger().error("Error while writing module configuration: " + this.getLabel());
            }
         }
      }

      return object;
   }

   private JsonArray getJsonElements(ItemListValue itemList) {
      JsonArray jsonArray = new JsonArray();
      ArrayList<ToggleableItem> list = (ArrayList)itemList.getValue();
      list.forEach((item) -> {
         if (item != null) {
            String var10001 = String.valueOf(Registries.ITEM.get(Identifier.of(item.getItemRegistryEntry().getValue().getPath().replace("minecraft:", "").replace(" ", "_").toLowerCase())));
            jsonArray.add(var10001 + "|" + item.isEnabled());
         }

      });
      return jsonArray;
   }

   private JsonArray getJsonElements(BlockListValue blockList) {
      JsonArray jsonArray = new JsonArray();
      ArrayList<ToggleableBlock> list = (ArrayList)blockList.getValue();
      list.forEach((block) -> {
         if (block.getBlock() != null) {
            String var10001 = String.valueOf(Registries.ITEM.get(Identifier.of(block.getBlock().toString().replace("Block{", "").replace("}", "").replace("minecraft:", ""))));
            jsonArray.add(var10001 + "|" + block.isEnabled());
         }

      });
      return jsonArray;
   }

   private JsonArray getJsonElements(TargetValue enemyFindingValue) {
      TargetPreset enemyFindingPreset = enemyFindingValue.getValue();
      JsonArray jsonArray = new JsonArray();
      jsonArray.add(enemyFindingPreset.isTargetPlayers());
      jsonArray.add(enemyFindingPreset.isTargetFriendlies());
      jsonArray.add(enemyFindingPreset.isTargetMonsters());
      jsonArray.add(enemyFindingPreset.isIgnoreInvis());
      jsonArray.add(enemyFindingPreset.isIgnoreNaked());
      jsonArray.add(enemyFindingPreset.getTarget().name());
      return jsonArray;
   }

   public void load(Value val, JsonElement element) {
      if (val.getValue() instanceof Boolean) {
         val.setValue(element.getAsBoolean());
      } else if (val.getValue() instanceof Double && val instanceof NumberValue) {
         NumberValue<?> numberValue = (NumberValue)val;
         numberValue.setValueNoLimit(element.getAsDouble());
      } else if (val.getValue() instanceof Float && val instanceof NumberValue) {
         NumberValue<?> numberValue = (NumberValue)val;
         numberValue.setValueNoLimit(element.getAsFloat());
      } else if (val.getValue() instanceof Integer && val instanceof NumberValue) {
         NumberValue<?> numberValue = (NumberValue)val;
         numberValue.setValueNoLimit(element.getAsInt());
      } else if (val.getValue() instanceof String) {
         String str = element.getAsString();
         val.setValue(str.replace("_", " "));
      } else if (val.getValue() instanceof Keybind) {
         val.setValue(InputUtil.fromTranslationKey(element.getAsString()));
      } else if (val instanceof EnumValue) {
         val.setValue(EnumUtil.fromString((Enum)val.getValue(), element.getAsString()));
      } else {
         boolean isTargetMonsters;
         if (val.getValue() instanceof Color) {
            ColorValue colorProperty = (ColorValue)val;
            JsonArray array = element.getAsJsonArray();
            String hex = array.get(0).getAsString();
            isTargetMonsters = array.get(1).getAsBoolean();
            colorProperty.setValue(ColorUtil.hexToColor(hex));
            colorProperty.setGlobal(isTargetMonsters);
         } else if (val instanceof TargetValue) {
            TargetValue enemyFindingValue = (TargetValue)val;
            JsonArray array = element.getAsJsonArray();
            boolean isTargetPlayers = array.get(0).getAsBoolean();
            boolean isTargetFriendlies = array.get(1).getAsBoolean();
            isTargetMonsters = array.get(2).getAsBoolean();
            boolean isIgnoreInvis = array.get(3).getAsBoolean();
            boolean isIgnoreNaked = array.get(4).getAsBoolean();
            String targetMode = array.get(5).getAsString();
            Enum<?> lel = EnumUtil.fromString(enemyFindingValue.getTarget(), targetMode);
            TargetPreset preset = new TargetPreset(isTargetPlayers, isTargetMonsters, isTargetFriendlies, isIgnoreInvis, isIgnoreNaked, lel);
            enemyFindingValue.setValue(preset);
         } else {
            ArrayList blocks;
            if (val instanceof ItemListValue) {
               ItemListValue itemList = (ItemListValue)val;
               blocks = new ArrayList();
               element.getAsJsonArray().forEach((elem) -> {
                  String[] split = elem.getAsString().split("\\|");
                  Item item = ((Item)Registries.ITEM.get(Identifier.of(split[0].replace("minecraft:", "")))).asItem();
                  boolean toggled = Boolean.parseBoolean(split[1]);
                  if (item != null) {
                     blocks.add(new ToggleableItem(item, toggled));
                  }
               });
               itemList.setValue(blocks);
            } else if (val instanceof BlockListValue) {
               BlockListValue blockList = (BlockListValue)val;
               blocks = new ArrayList();
               element.getAsJsonArray().forEach((elem) -> {
                  String[] split = elem.getAsString().split("\\|");
                  Block block = Block.getBlockFromItem((Item)Registries.ITEM.get(Identifier.of(split[0].replace("minecraft:", ""))));
                  boolean toggled = Boolean.parseBoolean(split[1]);
                  if (block != null) {
                     blocks.add(new ToggleableBlock(block, toggled));
                  }
               });
               blockList.setValue(blocks);
            }
         }
      }

   }

   public String[] getAliases() {
      return this.aliases;
   }

   public Category getCategory() {
      return this.category;
   }

   public ArrayList<Value<?>> getValues() {
      return this.values;
   }

   public ArrayList<Preset> getPresets() {
      return this.presets;
   }

   public List<Listener> getListeners() {
      return this.listeners;
   }

   public String getDisplayLabel() {
      return this.displayLabel;
   }

   public boolean isDrawn() {
      return this.drawn;
   }

   public void setDisplayLabel(String displayLabel) {
      this.displayLabel = displayLabel;
   }

   public void setDrawn(boolean drawn) {
      this.drawn = drawn;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Module)) {
         return false;
      } else {
         Module other = (Module)o;
         if (!other.canEqual(this)) {
            return false;
         } else if (this.isDrawn() != other.isDrawn()) {
            return false;
         } else if (!Arrays.deepEquals(this.getAliases(), other.getAliases())) {
            return false;
         } else {
            label76: {
               Object this$category = this.getCategory();
               Object other$category = other.getCategory();
               if (this$category == null) {
                  if (other$category == null) {
                     break label76;
                  }
               } else if (this$category.equals(other$category)) {
                  break label76;
               }

               return false;
            }

            Object this$values = this.getValues();
            Object other$values = other.getValues();
            if (this$values == null) {
               if (other$values != null) {
                  return false;
               }
            } else if (!this$values.equals(other$values)) {
               return false;
            }

            label62: {
               Object this$presets = this.getPresets();
               Object other$presets = other.getPresets();
               if (this$presets == null) {
                  if (other$presets == null) {
                     break label62;
                  }
               } else if (this$presets.equals(other$presets)) {
                  break label62;
               }

               return false;
            }

            label55: {
               Object this$listeners = this.getListeners();
               Object other$listeners = other.getListeners();
               if (this$listeners == null) {
                  if (other$listeners == null) {
                     break label55;
                  }
               } else if (this$listeners.equals(other$listeners)) {
                  break label55;
               }

               return false;
            }

            Object this$displayLabel = this.getDisplayLabel();
            Object other$displayLabel = other.getDisplayLabel();
            if (this$displayLabel == null) {
               if (other$displayLabel != null) {
                  return false;
               }
            } else if (!this$displayLabel.equals(other$displayLabel)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof Module;
   }

   public int hashCode() {
      int result = 1;
      result = result * 59 + (this.isDrawn() ? 79 : 97);
      result = result * 59 + Arrays.deepHashCode(this.getAliases());
      Object $category = this.getCategory();
      result = result * 59 + ($category == null ? 43 : $category.hashCode());
      Object $values = this.getValues();
      result = result * 59 + ($values == null ? 43 : $values.hashCode());
      Object $presets = this.getPresets();
      result = result * 59 + ($presets == null ? 43 : $presets.hashCode());
      Object $listeners = this.getListeners();
      result = result * 59 + ($listeners == null ? 43 : $listeners.hashCode());
      Object $displayLabel = this.getDisplayLabel();
      result = result * 59 + ($displayLabel == null ? 43 : $displayLabel.hashCode());
      return result;
   }

   public String toString() {
      String var10000 = Arrays.deepToString(this.getAliases());
      return "Module(aliases=" + var10000 + ", category=" + String.valueOf(this.getCategory()) + ", values=" + String.valueOf(this.getValues()) + ", presets=" + String.valueOf(this.getPresets()) + ", listeners=" + String.valueOf(this.getListeners()) + ", displayLabel=" + this.getDisplayLabel() + ", drawn=" + this.isDrawn() + ")";
   }
}
