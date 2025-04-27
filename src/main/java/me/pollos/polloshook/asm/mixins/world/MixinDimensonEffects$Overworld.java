package me.pollos.polloshook.asm.mixins.world;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import net.minecraft.client.render.DimensionEffects.Overworld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Overworld.class})
public class MixinDimensonEffects$Overworld {
   @Inject(
      method = {"adjustFogColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void adjustFogColorHook(Vec3d color, float sunHeight, CallbackInfoReturnable<Vec3d> cir) {
      CustomSky CUSTOM_SKY_MODULE = (CustomSky)Managers.getModuleManager().get(CustomSky.class);
      if (CUSTOM_SKY_MODULE.isFogMode()) {
         cir.setReturnValue(new Vec3d((double)((float)CUSTOM_SKY_MODULE.getFogColor().getColor().getRed() / 255.0F), (double)((float)CUSTOM_SKY_MODULE.getFogColor().getColor().getGreen() / 255.0F), (double)((float)CUSTOM_SKY_MODULE.getFogColor().getColor().getBlue() / 255.0F)));
      }

   }
}
