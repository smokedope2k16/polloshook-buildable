package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.PollosHook;
import me.pollos.polloshook.impl.events.render.RenderNametagEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public abstract class MixinEntityRenderer {
   @Inject(
      method = {"renderLabelIfPresent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderLabelIfPresentHook(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
      RenderNametagEvent renderNametagEvent = new RenderNametagEvent(entity);
      PollosHook.getEventBus().dispatch(renderNametagEvent);
      if (renderNametagEvent.isCanceled()) {
         ci.cancel();
      }

   }
}
