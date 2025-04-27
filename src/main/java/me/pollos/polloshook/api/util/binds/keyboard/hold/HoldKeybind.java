package me.pollos.polloshook.api.util.binds.keyboard.hold;

import me.pollos.polloshook.api.util.binds.keyboard.impl.Keybind;

public abstract class HoldKeybind extends Keybind {
   public HoldKeybind(int key) {
      super(key);
   }

   public abstract void onKeyHold();

   public abstract void onKeyRelease();
}
