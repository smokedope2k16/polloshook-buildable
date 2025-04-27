package me.pollos.polloshook.asm.mixins.util;

import me.pollos.polloshook.impl.events.keyboard.InputKeyDownEvent;
import me.pollos.polloshook.impl.module.render.freecam.Freecam;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardInput.class})
public class MixinKeyboardInput {
   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void tickHook(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
      Freecam.TickInputEvent event = Freecam.TickInputEvent.create();
      event.dispatch();
   }

   @Redirect(
      method = "tick",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"
      )
  )
  private boolean tickHook(KeyBinding instance) {
      InputKeyDownEvent event = InputKeyDownEvent.of((KeyboardInput)(Object)this, instance, instance.isPressed());
      event.dispatch();
      return event.isPressed();
  }

}
