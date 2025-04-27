package me.pollos.polloshook.asm.mixins.world;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import net.minecraft.client.render.DimensionEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DimensionEffects.class})
public abstract class MixinDimensionEffects {
   @Inject(
      method = {"getFogColorOverride"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getFogColorOverrideHook(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> cir) {
      CustomSky CUSTOM_SKY_MODULE = (CustomSky)Managers.getModuleManager().get(CustomSky.class);
      if (CUSTOM_SKY_MODULE.isFogMode()) {
         cir.setReturnValue(new float[]{(float)CUSTOM_SKY_MODULE.getFogColor().getColor().getRed() / 255.0F, (float)CUSTOM_SKY_MODULE.getFogColor().getColor().getGreen() / 255.0F, (float)CUSTOM_SKY_MODULE.getFogColor().getColor().getBlue() / 255.0F, 1.0F});
      }

   }
}
