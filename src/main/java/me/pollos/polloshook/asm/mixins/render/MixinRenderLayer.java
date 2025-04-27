package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.module.render.glintmodify.GlintModify;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderLayer.class})
public class MixinRenderLayer {
   @Inject(
      method = {"draw"},
      at = {@At("RETURN")}
   )
   public void drawHook(BuiltBuffer buffer, CallbackInfo ci) {
      if (GlintModify.china) {
         RenderMethods.resetColor();
         GlintModify.china = false;
      }

   }
}
