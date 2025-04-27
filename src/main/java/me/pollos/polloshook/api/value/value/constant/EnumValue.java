package me.pollos.polloshook.api.value.value.constant;

import java.util.StringJoiner;
import java.util.function.Supplier;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.manager.internal.CommandManager;
import me.pollos.polloshook.impl.module.player.yaw.mode.YawMode;
import net.minecraft.util.Formatting;

public class EnumValue<T extends Enum<?>> extends Value<T> {
   public EnumValue(T value, String... names) {
      super(value, names);
   }

   public EnumValue<T> setParent(Value<Boolean> parent) {
      super.setParent(parent);
      return this;
   }

   public EnumValue<T> setParent(Value<Boolean> parent, boolean opposite) {
      super.setParent(parent, opposite);
      return this;
   }

   public EnumValue<T> setParent(EnumValue<?> parent, Enum<?> target) {
      super.setParent(parent, target);
      return this;
   }

   public EnumValue<T> setParent(EnumValue<?> parent, Enum<?> target, boolean opposite) {
      super.setParent(parent, target, opposite);
      return this;
   }

   public EnumValue<T> setParent(Supplier<Boolean> parent, boolean opposite) {
      super.setParent(parent, opposite);
      return this;
   }

   public String returnValue(String[] args) {
      Module mod = Managers.getCommandManager().getModule(args);
      if (!args[2].equalsIgnoreCase("list")) {
         this.setValueFromString(args[2]);
         return CommandManager.setMessage(mod, this, this.getFixedValue());
      } else {
         StringJoiner stringJoiner = new StringJoiner(", ");
         Enum<?>[] array = (Enum[])((Enum)this.getValue()).getClass().getEnumConstants();
         Enum[] var5 = array;
         int var6 = array.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Enum<?> enumArray = var5[var7];
            Object[] var10002 = new Object[]{enumArray.name().equalsIgnoreCase(((Enum)this.getValue()).toString()) ? Formatting.GREEN : Formatting.RED, null};
            String var10005 = this.getFixedValue(enumArray);
            var10002[1] = var10005 + String.valueOf(Formatting.GRAY);
            stringJoiner.add(String.format("%s%s", var10002));
         }

         return "%s%s%s val %s%s%s modes (%s) %s".formatted(new Object[]{Formatting.YELLOW, mod.getLabel(), Formatting.GRAY, Formatting.AQUA, this.getLabel(), Formatting.GRAY, array.length, stringJoiner});
      }
   }

   public String getStylizedName() {
      return this.getStylizedName((Enum)this.value);
   }

   public String getStylizedName(Enum<?> e) {
      String originalValue = e.name().toLowerCase();
      StringBuilder builder = new StringBuilder();
      if (e.name().equalsIgnoreCase("b_version")) {
         return "v2.8.5";
      } else if (e.name().equalsIgnoreCase("v_version")) {
         return "v2.8.5";
      } else if (e.name().equalsIgnoreCase("abc")) {
         return "ABC";
      } else if (e.name().equalsIgnoreCase("IRC")) {
         return "IRC";
      } else if (e.name().equalsIgnoreCase("ONE_12_2")) {
         return "1.12.2";
      } else if (e instanceof YawMode) {
         YawMode mode = (YawMode)e;
         return mode.equals(YawMode.DEGREE_45) ? "45°" : "90°";
      } else {
         boolean cap = true;
         char[] var5 = originalValue.toCharArray();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            char c = var5[var7];
            if (c == '_') {
               cap = true;
            } else if (cap) {
               builder.append(Character.toUpperCase(c));
               cap = false;
            } else {
               builder.append(c);
            }
         }

         return builder.toString();
      }
   }

   public String getFixedValue() {
      char var10000 = ((Enum)this.value).name().charAt(0);
      return var10000 + ((Enum)this.value).name().toLowerCase().replaceFirst(Character.toString(((Enum)this.value).name().charAt(0)).toLowerCase(), "");
   }

   public void setValueFromString(String string) {
      Enum<?> entry = EnumUtil.fromString((Enum)this.value, string);
      this.setValue((T)entry);
   }

   private String getFixedValue(Enum<?> enumValue) {
      char var10000 = enumValue.name().charAt(0);
      return var10000 + enumValue.name().toLowerCase().replace(Character.toString(enumValue.name().charAt(0)).toLowerCase(), "");
   }

   public void increment() {
      Enum<?>[] array = (Enum[])((Enum)this.getValue()).getClass().getEnumConstants();
      int length = array.length;

      for(int i = 0; i < length; ++i) {
         if (array[i].name().equalsIgnoreCase(this.getFixedValue())) {
            ++i;
            if (i > array.length - 1) {
               i = 0;
            }

            this.setValue((T)array[i]);
         }
      }

   }



   public void decrement() {
      Enum<?>[] array = (Enum[])((Enum)this.getValue()).getClass().getEnumConstants();
      int length = array.length;

      for(int i = 0; i < length; ++i) {
         if (array[i].name().equalsIgnoreCase(this.getFixedValue())) {
            --i;
            if (i < 0) {
               i = array.length - 1;
            }

            this.setValue((T)array[i]);
         }
      }

   }
}