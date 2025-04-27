package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.render.GetCapeTextureEvent;
import me.pollos.polloshook.impl.events.render.RenderCapeEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CapeFeatureRenderer.class})
public class MixinCapeFeatureRenderer {
   @Inject(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderHook(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
      RenderCapeEvent renderCapeEvent = new RenderCapeEvent(abstractClientPlayerEntity);
      PollosHook.getEventBus().dispatch(renderCapeEvent);
      if (renderCapeEvent.isCanceled()) {
         ci.cancel();
      }
    }
    
    @Redirect(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTextures()Lnet/minecraft/client/util/SkinTextures;"
      )
    )
    private SkinTextures renderHook(AbstractClientPlayerEntity instance) {
      GetCapeTextureEvent getCapeTextureEvent = new GetCapeTextureEvent(
         instance, 
         new Identifier[]{
            instance.getSkinTextures().capeTexture(), 
            instance.getSkinTextures().elytraTexture()
         }
      );
      
      PollosHook.getEventBus().dispatch(getCapeTextureEvent);
      
      return new SkinTextures(
         instance.getSkinTextures().texture(), 
         instance.getSkinTextures().textureUrl(), 
         getCapeTextureEvent.getIdentifiers()[0], 
         getCapeTextureEvent.getIdentifiers()[1], 
         instance.getSkinTextures().model(), 
         instance.getSkinTextures().secure()
      );
   }
}