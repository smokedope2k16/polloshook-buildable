package me.pollos.polloshook.impl.events.keyboard;


import me.pollos.polloshook.api.event.events.Event;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;

public class InputKeyDownEvent extends Event {
   private final Input input;
   private final KeyBinding binding;
   private boolean pressed;

   
   public Input getInput() {
      return this.input;
   }

   
   public KeyBinding getBinding() {
      return this.binding;
   }

   
   public boolean isPressed() {
      return this.pressed;
   }

   
   public void setPressed(boolean pressed) {
      this.pressed = pressed;
   }

   
   private InputKeyDownEvent(Input input, KeyBinding binding, boolean pressed) {
      this.input = input;
      this.binding = binding;
      this.pressed = pressed;
   }

   
   public static InputKeyDownEvent of(Input input, KeyBinding binding, boolean pressed) {
      return new InputKeyDownEvent(input, binding, pressed);
   }
}
