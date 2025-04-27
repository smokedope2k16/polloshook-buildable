package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.render.RenderEndCrystalEvent;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EndCrystalEntityRenderer.class})
public class MixinEndCrystalEntityRenderer {
   @Final
   @Shadow
   private ModelPart core;
   @Final
   @Shadow
   private ModelPart frame;
   @Final
   @Shadow
   private ModelPart bottom;

   @Inject(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderPreHook(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
      RenderEndCrystalEvent.Pre pre = new RenderEndCrystalEvent.Pre(endCrystalEntity, matrixStack, vertexConsumerProvider, g, i, this.core, this.frame, this.bottom);
      PollosHook.getEventBus().dispatch(pre);
      if (pre.isCanceled()) {
         RenderEndCrystalEvent.Post post = new RenderEndCrystalEvent.Post(endCrystalEntity, matrixStack, vertexConsumerProvider, g, i, this.core, this.frame, this.bottom);
         PollosHook.getEventBus().dispatch(post);
         info.cancel();
      }

   }

   @Inject(
      method = {"render(Lnet/minecraft/entity/decoration/EndCrystalEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("RETURN")}
   )
   public void renderPostHook(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
      RenderEndCrystalEvent.Post post = new RenderEndCrystalEvent.Post(endCrystalEntity, matrixStack, vertexConsumerProvider, g, i, this.core, this.frame, this.bottom);
      PollosHook.getEventBus().dispatch(post);
   }
}
