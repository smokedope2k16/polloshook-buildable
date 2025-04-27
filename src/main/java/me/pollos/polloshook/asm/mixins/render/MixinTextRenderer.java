package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.impl.module.other.hud.HUD;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({TextRenderer.class})
public abstract class MixinTextRenderer {
   @Shadow
   public abstract String mirror(String var1);

   @Shadow
   private static int tweakTransparency(int argb) {
      return (argb & -67108864) == 0 ? argb | -16777216 : argb;
   }

   @Shadow
   protected abstract float drawLayer(String var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, VertexConsumerProvider var7, TextLayerType var8, int var9, int var10);

   @Shadow
   protected abstract float drawLayer(OrderedText var1, float var2, float var3, int var4, boolean var5, Matrix4f var6, VertexConsumerProvider var7, TextLayerType var8, int var9, int var10);

   @Overwrite
   private int drawInternal(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light, boolean mirror) {
      if (mirror) {
         text = this.mirror(text);
      }

      float x2 = x;
      float y2 = y;
      if (!((HUD)Managers.getModuleManager().get(HUD.class)).drawShadows()) {
         x2 = x - 0.4F;
         y2 = y - 0.4F;
      }

      color = tweakTransparency(color);
      Matrix4f matrix4f = new Matrix4f(matrix);
      if (shadow) {
         this.drawLayer(text, x2, y2, color, true, matrix, vertexConsumers, layerType, backgroundColor, light);
         matrix4f.translate(0.0F, 0.0F, 0.3F);
      }

      x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumers, layerType, backgroundColor, light);
      return (int)(x + (float)(shadow ? 1 : 0));
   }

   @Overwrite
   private int drawInternal(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumerProvider, TextLayerType layerType, int backgroundColor, int light) {
      color = tweakTransparency(color);
      float x2 = x;
      float y2 = y;
      if (!((HUD)Managers.getModuleManager().get(HUD.class)).drawShadows()) {
         x2 = x - 0.4F;
         y2 = y - 0.4F;
      }

      Matrix4f matrix4f = new Matrix4f(matrix);
      if (shadow) {
         this.drawLayer(text, x2, y2, color, true, matrix, vertexConsumerProvider, layerType, backgroundColor, light);
         matrix4f.translate(0.0F, 0.0F, 0.3F);
      }

      x = this.drawLayer(text, x, y, color, false, matrix4f, vertexConsumerProvider, layerType, backgroundColor, light);
      return (int)x + (shadow ? 1 : 0);
   }
}
