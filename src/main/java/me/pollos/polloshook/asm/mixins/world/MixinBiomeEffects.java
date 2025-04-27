package me.pollos.polloshook.asm.mixins.world;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BiomeEffects.class})
public class MixinBiomeEffects {
   @Inject(
      method = {"getFogColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getFogColorHook(CallbackInfoReturnable<Integer> cir) {
      if (((CustomSky)Managers.getModuleManager().get(CustomSky.class)).isFogMode()) {
         cir.setReturnValue(((CustomSky)Managers.getModuleManager().get(CustomSky.class)).getFogColor().getColor().getRGB());
      }

   }

   @Inject(
      method = {"getSkyColor"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSkyColorHook(CallbackInfoReturnable<Integer> cir) {
      if (((CustomSky)Managers.getModuleManager().get(CustomSky.class)).isSkyMode()) {
         cir.setReturnValue(((CustomSky)Managers.getModuleManager().get(CustomSky.class)).getSkyColor().getColor().getRGB());
      }

   }
}
