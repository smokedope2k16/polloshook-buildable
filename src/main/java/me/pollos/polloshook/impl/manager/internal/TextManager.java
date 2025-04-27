package me.pollos.polloshook.impl.manager.internal;

import java.awt.Font;
import java.util.Objects;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.minecraft.render.utils.FontUtil;
import me.pollos.polloshook.api.util.font.FontAdapter;
import me.pollos.polloshook.api.util.font.RendererFontAdapter;
import me.pollos.polloshook.impl.module.misc.nameprotect.NameProtect;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class TextManager implements Minecraftable {
   private FontAdapter fontAdapter;
   private boolean custom;

   public void init() {
      this.set("Tahoma", 0, 9.0F);
   }

   public void set(String name, int style, float size) {
      this.fontAdapter = new RendererFontAdapter(new Font(name, style, (int)size), size);
   }

   public void drawString(DrawContext context, String text, double x, double y, int color) {
      if (this.custom) {
         this.fontAdapter.drawString(context.getMatrices(), NameProtect.nameProtect(text), (float)((int)x), (float)((int)y) + 1.0F, color);
      } else {
         context.drawText(mc.textRenderer, text, (int)x, (int)y, color, true);
      }

   }

   public void drawString(MatrixStack matrixStack, String text, double x, double y, int color) {
      if (this.custom) {
         this.fontAdapter.drawString(matrixStack, NameProtect.nameProtect(text), (float)x - 1.0F, (float)y + 1.0F, color);
      } else {
         FontUtil.drawStringWithShadow(matrixStack, text, (float)x, (float)y, color);
      }

   }

   public float getWidth(String s) {
      return this.custom ? this.fontAdapter.getWidth(s) : (float)mc.textRenderer.getWidth(s);
   }

   public int getHeight() {
      int var10000;
      if (this.custom) {
         var10000 = Math.round(this.fontAdapter.getFontHeight()) - 2;
      } else {
         Objects.requireNonNull(mc.textRenderer);
         var10000 = 9;
      }

      return var10000;
   }

   public int getHeight(String s) {
      int var10000;
      if (this.custom) {
         var10000 = Math.round(this.fontAdapter.getFontHeight(s));
      } else {
         Objects.requireNonNull(mc.textRenderer);
         var10000 = 9;
      }

      return var10000;
   }

   public float getHeightScale(String s) {
      return this.custom ? 2.0F : 1.0F;
   }

   
   public void setCustom(boolean custom) {
      this.custom = custom;
   }

   
   public boolean isCustom() {
      return this.custom;
   }
}
