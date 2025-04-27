package me.pollos.polloshook.asm.mixins.util;

import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({InputUtil.class})
public class MixinInputUtil {
   @Inject(
      method = {"isKeyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void isKeyPressedHook(long window, int code, CallbackInfoReturnable<Boolean> cir) {
      if (code == -1 || code == 0) {
         cir.setReturnValue(false);
      }

   }
}
