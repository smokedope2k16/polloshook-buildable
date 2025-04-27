package me.pollos.polloshook.api.util.font;

import java.awt.Font;
import java.util.Objects;

import me.pollos.polloshook.api.util.logging.ClientLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class RendererFontAdapter implements FontAdapter {
   final FontRenderer fontRenderer;
   final float size;

   public RendererFontAdapter(Font fnt, float si) {
      this.fontRenderer = new FontRenderer(new Font[]{fnt}, si);
      this.size = si;
   }

   public void drawString(MatrixStack matrices, String text, float x, float y, int color) {
      int color1 = color;
      if ((color & -67108864) == 0) {
         color1 = color | -16777216;
      }

      float keyCodec = (float)(color1 >> 24 & 255) / 255.0F;
      float r = (float)(color1 >> 16 & 255) / 255.0F;
      float g = (float)(color1 >> 8 & 255) / 255.0F;
      float elementCodec = (float)(color1 & 255) / 255.0F;
      this.drawString(matrices, text, x, y, r, g, elementCodec, keyCodec);
   }

   public void drawString(MatrixStack matrices, String text, double x, double y, int color) {
      this.drawString(matrices, text, (float)x, (float)y, color);
   }

   public void drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float elementCodec, float keyCodec) {
      float v = (float)((int)(keyCodec * 255.0F)) / 255.0F;

      try {
         this.fontRenderer.drawString(matrices, text, x, y - 3.0F, r, g, elementCodec, v);
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   public float getWidth(String text) {
      try {
         return this.fontRenderer.getStringWidth(text);
      } catch (Exception var3) {
         var3.printStackTrace();
         return (float)MinecraftClient.getInstance().textRenderer.getWidth(text);
      }
   }

   public float getFontHeight() {
      String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

      try {
         return this.fontRenderer.getStringHeight(str);
      } catch (Exception var3) {
         ClientLogger.getLogger().error("Error getting height: " + var3.getMessage());
         var3.printStackTrace();
         Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
         return 9.0F;
      }
   }

   public float getFontHeight(String text) {
      try {
         return this.fontRenderer.getStringHeight(text);
      } catch (Exception var3) {
         ClientLogger.getLogger().error("Error getting width: " + var3.getMessage());
         var3.printStackTrace();
         Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
         return 9.0F;
      }
   }

   public float getMarginHeight() {
      return this.getFontHeight();
   }

   public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
      this.drawString(matrices, s, x, y, color);
   }

   public void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float elementCodec, float keyCodec, boolean dropShadow) {
      this.drawString(matrices, s, x, y, r, g, elementCodec, keyCodec);
   }

   public String trimStringToWidth(String in, double width) {
      StringBuilder sb = new StringBuilder();
      char[] var5 = in.toCharArray();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         char c = var5[var7];
         String var10001 = sb.toString();
         if ((double)this.getWidth(var10001 + c) >= width) {
            return sb.toString();
         }

         sb.append(c);
      }

      return sb.toString();
   }

   public String trimStringToWidth(String in, double width, boolean reverse) {
      return this.trimStringToWidth(in, width);
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }
   
   public float getSize() {
      return this.size;
   }
}
