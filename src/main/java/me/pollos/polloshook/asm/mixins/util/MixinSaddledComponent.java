package me.pollos.polloshook.asm.mixins.util;

import me.pollos.polloshook.impl.module.movement.entitycontrol.EntityControl;
import net.minecraft.entity.SaddledComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SaddledComponent.class})
public class MixinSaddledComponent {
   @Inject(
      method = {"isSaddled"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void isSaddledHook(CallbackInfoReturnable<Boolean> cir) {
      EntityControl.SaddleEvent event = new EntityControl.SaddleEvent();
      event.dispatch();
      if (event.isCanceled()) {
         cir.setReturnValue(true);
      }

   }
}
