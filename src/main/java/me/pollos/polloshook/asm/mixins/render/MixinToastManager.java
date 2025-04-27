package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ToastManager.class})
public class MixinToastManager {
   @Inject(
      method = {"draw"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void drawHook(DrawContext context, CallbackInfo ci) {
      NoRender.ToastsEvent event = NoRender.ToastsEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }
}
