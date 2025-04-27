package me.pollos.polloshook.asm.mixins.render;

import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.impl.module.render.modelchanger.ModelChanger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper.Argb;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   @Final
   @Shadow
   private ItemColors colors;

   @Inject(
      method = {"renderBakedItemQuads"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderBakedItemQuadsHook(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay, CallbackInfo info) {
      ModelChanger.ItemAlphaEvent event = ModelChanger.ItemAlphaEvent.create();
      event.dispatch();
      if (event.isCanceled() && ((me.pollos.polloshook.asm.ducks.world.IGameRenderer)MinecraftClient.getInstance().gameRenderer).isRenderingHand()) {
         boolean bl = !stack.isEmpty();
         Entry entry = matrices.peek();
         Iterator var11 = quads.iterator();

         while(var11.hasNext()) {
            BakedQuad bakedQuad = (BakedQuad)var11.next();
            int i = -1;
            if (bl && bakedQuad.hasColor()) {
               i = this.colors.getColor(stack, bakedQuad.getColorIndex());
            }

            float g = (float)Argb.getRed(i) / 255.0F;
            float h = (float)Argb.getGreen(i) / 255.0F;
            float j = (float)Argb.getBlue(i) / 255.0F;
            vertices.quad(entry, bakedQuad, g, h, j, (float)event.getAlpha() / 255.0F, light, overlay);
         }

         info.cancel();
      }

   }
}
