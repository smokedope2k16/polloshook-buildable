package me.pollos.polloshook.asm.mixins.gui;

import me.pollos.polloshook.impl.module.render.norender.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({InGameOverlayRenderer.class})
public class MixinInGameOverlayRenderer {
   @Inject(
      method = {"renderFireOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void renderFireOverlayHook(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo info) {
      NoRender.FireOverlayEvent event = NoRender.FireOverlayEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         info.cancel();
      }

   }

   @Inject(
      method = {"renderInWallOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void renderInWallOverlayHook(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
      NoRender.SuffocationOverlayEvent event = NoRender.SuffocationOverlayEvent.create();
      event.dispatch();
      if (event.isCanceled()) {
         ci.cancel();
      }

   }
}
