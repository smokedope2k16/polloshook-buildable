package me.pollos.polloshook.api.value.value;

import java.util.function.Supplier;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;
import me.pollos.polloshook.impl.manager.internal.CommandManager;

public class NumberValue<T extends Number> extends Value<T> {
   private T minimum;
   private T maximum;
   private T steps;
   private String tag;
   private boolean noLimit = false;

   public NumberValue(T value, T min, T max, String... names) {
      super(value, names);
      this.minimum = min;
      this.maximum = max;
   }

   public NumberValue(T value, T min, T max, T steps, String... names) {
      super(value, names);
      this.minimum = min;
      this.maximum = max;
      this.steps = steps;
   }

   public NumberValue<T> setParent(Parent parent) {
      super.setParent(parent);
      return this;
   }

   public NumberValue<T> setParent(Value<Boolean> parent) {
      super.setParent(parent);
      return this;
   }

   public NumberValue<T> setParent(Value<Boolean> parent, boolean opposite) {
      super.setParent(parent, opposite);
      return this;
   }

   public NumberValue<T> setParent(EnumValue<?> parent, Enum<?> target) {
      super.setParent(parent, target);
      return this;
   }

   public NumberValue<T> setParent(EnumValue<?> parent, Enum<?> target, boolean opposite) {
      super.setParent(parent, target, opposite);
      return this;
   }

   public NumberValue<T> setParent(Supplier<Boolean> parent) {
      super.setParent(parent);
      return this;
   }

   public NumberValue<T> withTag(String tag) {
      this.tag = tag;
      return this;
   }

   public void setValue(T value) {
      if (this.maximum != null && this.minimum != null) {
         if (value instanceof Integer) {
            if (value.intValue() > this.maximum.intValue()) {
               value = this.maximum;
            } else if (value.intValue() < this.minimum.intValue()) {
               value = this.minimum;
            }
         } else if (value instanceof Float) {
            if (value.floatValue() > this.maximum.floatValue()) {
               value = this.maximum;
            } else if (value.floatValue() < this.minimum.floatValue()) {
               value = this.minimum;
            }
         } else if (value instanceof Double) {
            if (value.doubleValue() > this.maximum.doubleValue()) {
               value = this.maximum;
            } else if (value.doubleValue() < this.minimum.doubleValue()) {
               value = this.minimum;
            }
         } else if (value instanceof Long) {
            if (value.longValue() > this.maximum.longValue()) {
               value = this.maximum;
            } else if (value.longValue() < this.minimum.longValue()) {
               value = this.minimum;
            }
         } else if (value instanceof Short) {
            if (value.shortValue() > this.maximum.shortValue()) {
               value = this.maximum;
            } else if (value.shortValue() < this.minimum.shortValue()) {
               value = this.minimum;
            }
         } else if (value instanceof Byte) {
            if (value.byteValue() > this.maximum.byteValue()) {
               value = this.maximum;
            } else if (value.byteValue() < this.minimum.byteValue()) {
               value = this.minimum;
            }
         }
      }

      super.setValue(value);
   }

   public String returnValue(String[] args) {
      Module mod = Managers.getCommandManager().getModule(args);
      if (!args[2].equalsIgnoreCase("get")) {
         if (this.getValue() instanceof Double) {
            this.setValueNoLimit(Double.parseDouble(args[2]));
         }

         if (this.getValue() instanceof Integer) {
            this.setValueNoLimit(Integer.parseInt(args[2]));
         }

         if (this.getValue() instanceof Float) {
            this.setValueNoLimit(Float.parseFloat(args[2]));
         }

         if (this.getValue() instanceof Long) {
            this.setValueNoLimit(Long.parseLong(args[2]));
         }

         return CommandManager.setMessage(mod, this, this.getValue());
      } else {
         return CommandManager.currentMessage(mod, this, this.getValue());
      }
   }

   private void set(Number number) {
      this.setValue((T) number);
   }

   public void setValueNoLimit(Number number) {
      if (this.noLimit) {
         super.setValue((T)number);
      } else {
         this.set(number);
      }

   }

   
   public T getMinimum() {
      return this.minimum;
   }

   
   public T getMaximum() {
      return this.maximum;
   }

   
   public T getSteps() {
      return this.steps;
   }

   
   public String getTag() {
      return this.tag;
   }

   
   public boolean isNoLimit() {
      return this.noLimit;
   }

   
   public NumberValue<T> setMinimum(T minimum) {
      this.minimum = minimum;
      return this;
   }

   
   public NumberValue<T> setMaximum(T maximum) {
      this.maximum = maximum;
      return this;
   }

   
   public NumberValue<T> setNoLimit(boolean noLimit) {
      this.noLimit = noLimit;
      return this;
   }
}
