package me.pollos.polloshook.asm.mixins.render;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.asm.ducks.render.IDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({DrawContext.class})
public abstract class MixinDrawContext implements IDrawContext {
   @Shadow
   @Final
   private MatrixStack matrices;
   @Shadow
   @Final
   private MinecraftClient client;

   @Shadow
   public abstract void fill(RenderLayer var1, int var2, int var3, int var4, int var5, int var6);

   @Unique
   public void drawItemInSlotCFont(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride) {
      if (!stack.isEmpty()) {
         this.matrices.push();
         if (stack.getCount() != 1 || countOverride != null) {
            String string = countOverride == null ? String.valueOf(stack.getCount()) : countOverride;
            this.matrices.translate(0.0F, 0.0F, 200.0F);
            Managers.getTextManager().drawString(this.matrices, string, (double)(x + 19 - 2 - textRenderer.getWidth(string)), (double)(y + 6 + 3), 16777215);
         }

         int l;
         int k;
         if (stack.isItemBarVisible()) {
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            k = x + 2;
            l = y + 13;
            this.fill(RenderLayer.getGuiOverlay(), k, l, k + 13, l + 2, -16777216);
            this.fill(RenderLayer.getGuiOverlay(), k, l, k + i, l + 1, j | -16777216);
         }

         ClientPlayerEntity clientPlayerEntity;
         float f = (clientPlayerEntity = this.client.player) == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getRenderTickCounter().getTickDelta(true));
         if (f > 0.0F) {
            k = y + MathHelper.floor(16.0F * (1.0F - f));
            l = k + MathHelper.ceil(16.0F * f);
            this.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
         }

         this.matrices.pop();
      }
   }
}
