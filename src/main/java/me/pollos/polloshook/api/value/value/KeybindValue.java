package me.pollos.polloshook.api.value.value;

import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;

public class KeybindValue extends Value<Keybind> {
   public KeybindValue(Keybind value, String... names) {
      super(value, names);
   }

   public KeybindValue setParent(Value<Boolean> parent) {
      super.setParent(parent);
      return this;
   }
}
