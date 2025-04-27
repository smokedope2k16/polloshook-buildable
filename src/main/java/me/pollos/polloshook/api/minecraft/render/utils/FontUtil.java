package me.pollos.polloshook.api.minecraft.render.utils;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;

public final class FontUtil implements Minecraftable {

   private FontUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static void drawStringWithShadow(MatrixStack matrixStack, String text, float x, float y, int color) {
      Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
      mc.textRenderer.draw(text, x, y, color, true, matrixStack.peek().getPositionMatrix(), immediate, TextLayerType.NORMAL, 0, 15728880, mc.textRenderer.isRightToLeft());
      immediate.draw();
   }
}