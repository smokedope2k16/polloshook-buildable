package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.module.render.glintmodify.GlintModify;
import net.minecraft.client.render.RenderPhase;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderPhase.class})
public class MixinRenderPhase {
   @Inject(
      method = {"setupGlintTexturing"},
      at = {@At("HEAD")}
   )
   private static void setupGlintTexturingHook(float scale, CallbackInfo info) {
      GlintModify ENCHANT_COLOR = (GlintModify)Managers.getModuleManager().get(GlintModify.class);
      if (ENCHANT_COLOR.isEnabled()) {
         if (!(Boolean)ENCHANT_COLOR.getArmour().getValue() && scale == 0.16F) {
            return;
         }

         RenderMethods.color(ENCHANT_COLOR.getColor().getColor().getRGB());
         GlintModify.china = true;
      }

   }

   @Redirect(
      method = {"setupGlintTexturing"},
      at = @At(
   value = "INVOKE",
   target = "Lorg/joml/Matrix4f;scale(F)Lorg/joml/Matrix4f;"
),
      remap = false
   )
   private static Matrix4f setupGlintTexturingHook(Matrix4f instance, float xyz) {
      GlintModify ENCHANT_COLOR = (GlintModify)Managers.getModuleManager().get(GlintModify.class);
      float angle = 0.17453292F;
      if (ENCHANT_COLOR.isEnabled()) {
         float scale = xyz == 0.16F ? (Float)ENCHANT_COLOR.getEntityScale().getValue() : (xyz == 8.0F ? (Float)ENCHANT_COLOR.getGlintScale().getValue() : xyz);
         return instance.rotateZ((Boolean)ENCHANT_COLOR.getRotateAngle().getValue() ? (Float)ENCHANT_COLOR.getAngle().getValue() : angle).scale(scale);
      } else {
         return instance.rotateZ(angle).scale(xyz);
      }
   }
}
