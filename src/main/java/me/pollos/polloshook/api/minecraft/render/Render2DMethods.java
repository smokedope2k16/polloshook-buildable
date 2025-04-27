package me.pollos.polloshook.api.minecraft.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.joml.Matrix4f;

public class Render2DMethods implements Minecraftable {
   public static void enable2D() {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
   }

   public static void disable2D() {
      RenderSystem.disableBlend();
   }

   public static void ding() {
      mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 0.3F));
   }

   public static void click() {
      mc.getSoundManager().play(PositionedSoundInstance.master((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
   }

   public static boolean mouseWithinBounds(double mouseX, double mouseY, Rectangle rect) {
      return mouseX >= (double)rect.getX() && mouseX <= (double)(rect.getX() + rect.getWidth()) && mouseY >= (double)rect.getY() && mouseY <= (double)(rect.getY() + rect.getHeight());
   }

   public static boolean mouseWithinBounds(double mouseX, double mouseY, double x, double y, double width, double height) {
      return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
   }

   public static void drawNameTagRect(MatrixStack stack, float left, float top, float right, float bottom, int color, int border, float width) {
      quickDrawRect(stack, left, top, right, bottom, color, false);
      RenderSystem.lineWidth(width);
      quickDrawRect(stack, left, top, right, bottom, border, true);
   }

   public static void quickDrawRect(MatrixStack stack, float x, float y, float x2, float y2, int color, boolean line) {
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      Matrix4f matrix = stack.peek().getPositionMatrix();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder bufferBuilder;
      if (line) {
         bufferBuilder = Tessellator.getInstance().begin(DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
         bufferBuilder.vertex(matrix, x, y2, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x, y, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x, y, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x2, y, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x2, y, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x, y2, 0.0F).color(r, g, b, alpha);
      } else {
         bufferBuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         bufferBuilder.vertex(matrix, x, y2, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x2, y2, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x2, y, 0.0F).color(r, g, b, alpha);
         bufferBuilder.vertex(matrix, x, y, 0.0F).color(r, g, b, alpha);
      }

      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   public static void drawCircle(DrawContext context, float x, float y, float radius, int color) {
      drawCircle(context, x, y, radius, 1.0F, color);
   }

   public static void drawCircle(DrawContext context, float x, float y, float radius, float progress, int color) {
      float alpha = (float)(color >> 24 & 255) / 255.0F;
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder bufferBuilder = Tessellator.getInstance().begin(DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
      int max = (int)(360.0F * progress);

      for(int i = 0; i <= max; ++i) {
         bufferBuilder.vertex(matrix, (float)((double)x + Math.sin((double)i * 3.141526D / 180.0D) * (double)radius), (float)((double)y + Math.cos((double)i * 3.141526D / 180.0D) * (double)radius), 0.0F).color(red, green, blue, alpha);
      }

      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
   }

   public static void drawRect(DrawContext context, Rectangle rect, int color) {
      drawRect(context, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), color);
   }

   public static void drawRect(DrawContext context, float x, float y, float width, float height, int color) {
      Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder bufferBuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferBuilder.vertex(matrix4f, x, y, 0.0F).color(color);
      bufferBuilder.vertex(matrix4f, x, height, 0.0F).color(color);
      bufferBuilder.vertex(matrix4f, width, height, 0.0F).color(color);
      bufferBuilder.vertex(matrix4f, width, y, 0.0F).color(color);
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
      RenderSystem.disableBlend();
   }

   public static void drawBorderedRect(DrawContext context, Rectangle rectangle, float lineSize, int color, int borderColor) {
      float x = rectangle.getX();
      float y = rectangle.getY();
      float width = rectangle.getWidth();
      float height = rectangle.getHeight();
      drawBorderedRect(context, x, y, width, height, lineSize, color, borderColor);
   }

   public static void drawBorderedRect(DrawContext context, float x, float y, float width, float height, float lineSize, int color, int borderColor) {
      drawRect(context, x, y, width, height, color);
      drawRect(context, x, y, x + lineSize, height, borderColor);
      drawRect(context, width - lineSize, y, width, height, borderColor);
      drawRect(context, x, height - lineSize, width, height, borderColor);
      drawRect(context, x, y, width, y + lineSize, borderColor);
   }

   public static void drawCheckeredBackground(DrawContext context, float x, float y, float x2, float y2) {
      drawRect(context, x, y, x2, y2, -1);

      for(boolean offset = false; y < y2; ++y) {
         for(float x1 = x + (float)((offset = !offset) ? 1 : 0); x1 < x2; x1 += 2.0F) {
            if (!(x1 > x2 - 1.0F)) {
               drawRect(context, x1, y, x1 + 1.0F, y + 1.0F, -8355712);
            }
         }
      }

   }

   public static void renderLine(MatrixStack stack, Color color, double x, double y, double x1, double y1) {
      Matrix4f m = stack.peek().getPositionMatrix();
      BufferBuilder bufferBuilder = Tessellator.getInstance().begin(DrawMode.LINES, VertexFormats.POSITION_COLOR);
      bufferBuilder.vertex(m, (float)x, (float)y, 0.0F).color(color.getRGB());
      bufferBuilder.vertex(m, (float)x1, (float)y1, 0.0F).color(color.getRGB());
      enable2D();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
      disable2D();
   }

   public static void drawGradientRect(DrawContext context, Rectangle rect, boolean sideways, int startColor, int endColor) {
      drawGradientRect(context, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), sideways, startColor, endColor);
   }

   public static void drawGradientRect(DrawContext context, float left, float top, float right, float bottom, boolean sideways, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorProgram);
      BufferBuilder bufferBuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      if (sideways) {
         bufferBuilder.vertex(matrix, left, top, 0.0F).color(f1, f2, f3, f);
         bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(f1, f2, f3, f);
         bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(f5, f6, f7, f4);
         bufferBuilder.vertex(matrix, right, top, 0.0F).color(f5, f6, f7, f4);
      } else {
         bufferBuilder.vertex(matrix, right, top, 0.0F).color(f1, f2, f3, f);
         bufferBuilder.vertex(matrix, left, top, 0.0F).color(f1, f2, f3, f);
         bufferBuilder.vertex(matrix, left, bottom, 0.0F).color(f5, f6, f7, f4);
         bufferBuilder.vertex(matrix, right, bottom, 0.0F).color(f5, f6, f7, f4);
      }

      BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
      RenderSystem.disableBlend();
   }

   public static void scissor(float x, float y, float x1, float y1) {
      double scale = mc.getWindow().getScaleFactor();
      RenderSystem.enableScissor((int)((double)x * scale), (int)((double)((float)mc.getWindow().getScaledHeight() - y1) * scale), (int)((double)(x1 - x) * scale), (int)((double)(y1 - y) * scale));
   }
}