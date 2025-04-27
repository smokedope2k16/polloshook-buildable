package me.pollos.polloshook.asm.mixins.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.render.customsky.CustomSky;
import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BackgroundRenderer.class})
public class MixinBackgroundRender {
   @Inject(
      method = {"applyFog"},
      at = {@At("TAIL")}
   )
   private static void applyFogHook(Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
      NoRender.FogEvent event = NoRender.FogEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         cancelFog(viewDistance);
      } else {
         CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
         boolean isLavaOrWater = cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER;
         if (isLavaOrWater) {
            NoRender.LiquidVisionEvent liquidEvent = NoRender.LiquidVisionEvent.create();
            liquidEvent.dispatch();
            if (liquidEvent.isCanceled()) {
               cancelFog(viewDistance);
               return;
            }
         }

         CustomSky CUSTOM_SKY = (CustomSky)Managers.getModuleManager().get(CustomSky.class);
         if (CUSTOM_SKY.isEnabled() && (Boolean)CUSTOM_SKY.getCustomFogRange().getValue() && fogType == FogType.FOG_TERRAIN) {
            float rang = (Float)CUSTOM_SKY.getRange().getValue() / 10.0F;
            RenderSystem.setShaderFogStart(viewDistance * rang / 10.0F);
            RenderSystem.setShaderFogEnd(viewDistance * rang + 0.25F);
         }

      }
   }

   @Unique
   private static void cancelFog(float fl) {
      RenderSystem.setShaderFogStart(fl * 4.0F);
      RenderSystem.setShaderFogEnd(fl * 4.25F);
   }
}