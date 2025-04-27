package me.pollos.polloshook.asm.mixins.keyboard;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.impl.events.keyboard.KeyPressEvent;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Keyboard.class})
public class MixinKeyboard {
   @Inject(
      method = {"onKey"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onKeyHook(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
      if (key != -1) {
         KeyPressEvent keyPressEvent = new KeyPressEvent(key, KeyboardUtil.getActionByInt(action));
         PollosHook.getEventBus().dispatch(keyPressEvent);
         if (keyPressEvent.isCanceled()) {
            ci.cancel();
         }

      }
   }
}
