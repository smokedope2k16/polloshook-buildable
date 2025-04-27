package me.pollos.polloshook.impl.gui.click.component.values;

import java.awt.Color;
import java.util.Objects;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.binds.keyboard.impl.KeyboardUtil;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.logging.ClientLogger;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class ColorComponent extends ValueComponent<Color, ColorValue> {
   private final ColorValue colorValue;
   private boolean colorExtended;
   private boolean colorSelectorDragging;
   private boolean alphaSelectorDragging;
   private boolean hueSelectorDragging;
   private float hue;
   private float saturation;
   private float brightness;
   private float alpha;
   private final int goHomeLilNigga = 154;
   private Color lastColor;
   private boolean renderAlpha;

   public ColorComponent(ColorValue colorValue, Rectangle rect, float offsetX, float offsetY) {
      super(colorValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), colorValue);
      this.colorValue = colorValue;
      float[] hsb = Color.RGBtoHSB(this.getColorValue().getColor().getRed(), this.getColorValue().getColor().getGreen(), this.getColorValue().getColor().getBlue(), (float[])null);
      this.hue = hsb[0];
      this.saturation = hsb[1];
      this.brightness = hsb[2];
      this.alpha = (float)this.getColorValue().getColor().getAlpha() / 255.0F;
      this.renderAlpha = true;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      boolean isColours = Colours.get().getValues().contains(this.colorValue);
      float fixedHeight = this.isColorExtended() ? (isColours ? this.getExtendedHeight() + 40.0F : this.getExtendedHeight()) : 14.0F;
      if ((Boolean)ClickGUI.get().getFutureBox().getValue()) {
         Render2DMethods.drawGradientRect(context, this.commonRenderRectangle().setHeight(this.getFinishedY() + fixedHeight), false, 553648127, 285212671);
      }

      Rectangle indicatorRectangle = new Rectangle(this.getFinishedX() + 1.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + 2.5F, this.getFinishedY() + this.getHeight() - 0.5F);
      Render2DMethods.drawRect(context, indicatorRectangle, this.getColor(200).getRGB());
      Render2DMethods.drawRect(context, this.isColorExtended() ? this.commonRenderRectangle().setHeight(this.getFinishedY() + 1.0F + 154.0F - (float)(isColours ? 50 : 46)) : this.commonRenderRectangle(), this.getColor().getRGB());
      Managers.getTextManager().drawString((DrawContext)context, this.getLabel(), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
      Render2DMethods.drawBorderedRect(context, this.getFinishedX() + this.getWidth() - 20.0F, this.getFinishedY() + 3.0F, this.getFinishedX() + this.getWidth() - 5.0F, this.getFinishedY() + 12.0F, 0.5F, this.getColorValue().getColor().getRGB(), -16777216);
      this.setHeight(this.isColorExtended() ? this.getExtendedHeight() : 14.0F);
      if (this.isColorExtended()) {
         float expandedX = this.getFinishedX() + 1.0F;
         float expandedY = this.getFinishedY() + 14.0F;
         float colorPickerLeft = expandedX + 4.0F;
         float colorPickerTop = expandedY + 1.0F;
         float colorPickerRight = colorPickerLeft + (this.getWidth() - 14.0F);
         float colorPickerBottom = colorPickerTop + 86.0F;
         int selectorWhiteOverlayColor = (new Color(255, 255, 255, 180)).getRGB();
         int colorMouseX = (int)MathUtil.clamp((float)mouseX, colorPickerLeft, colorPickerRight);
         int colorMouseY = (int)MathUtil.clamp((float)mouseY, colorPickerTop, colorPickerBottom);
         Render2DMethods.drawRect(context, colorPickerLeft - 0.5F, colorPickerTop - 0.5F, colorPickerRight + 0.5F, colorPickerBottom + 0.5F, -16777216);
         this.drawColorPickerRect(context, colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);
         float colorSelectorX = this.saturation * (colorPickerRight - colorPickerLeft);
         float colorSelectorY = (1.0F - this.brightness) * (colorPickerBottom - colorPickerTop);
         boolean colorSelectorHovering = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)colorPickerLeft, (double)(colorPickerTop - 32.0F), (double)(this.getWidth() - 20.0F), (double)(this.getHeight() - 36.0F));
         if (colorSelectorHovering) {
            try {
               String hex = ColorUtil.colorToHex(this.colorValue.getColor());
               ClientLogger var10000;
               String var10001;
               if (KeyboardUtil.isCopying()) {
                  mc.keyboard.setClipboard(hex);
                  var10000 = ClientLogger.getLogger();
                  var10001 = String.valueOf(Formatting.GREEN);
                  var10000.log(var10001 + "Copied Hex [%s] to clipboard".formatted(new Object[]{hex.toUpperCase()}));
               } else if (KeyboardUtil.isPasting()) {
                  var10000 = ClientLogger.getLogger();
                  var10001 = String.valueOf(Formatting.GREEN);
                  var10000.log(var10001 + "Pasted Hex [%s] into the %s".formatted(new Object[]{hex.toUpperCase(), this.getLabel()}));
                  Color hexFromClipboard = ColorUtil.hexToColor(mc.keyboard.getClipboard());
                  float[] colorArray = Color.RGBtoHSB(hexFromClipboard.getRed(), hexFromClipboard.getGreen(), hexFromClipboard.getBlue(), (float[])null);
                  this.updateFull(colorArray);
               }
            } catch (Exception var46) {
               var46.printStackTrace();
            }
         }

         float csBottom;
         float csLeft;
         float csTop;
         float csRight;
         if (this.colorSelectorDragging) {
            csLeft = colorPickerRight - colorPickerLeft;
            csTop = (float)colorMouseX - colorPickerLeft;
            this.saturation = csTop / csLeft;
            colorSelectorX = csTop;
            csRight = colorPickerBottom - colorPickerTop;
            csBottom = (float)colorMouseY - colorPickerTop;
            this.brightness = 1.0F - csBottom / csRight;
            colorSelectorY = csBottom;
            this.updateColor(this.getHex());
         }

         csLeft = colorPickerLeft + colorSelectorX - 0.5F;
         csTop = colorPickerTop + colorSelectorY - 0.5F;
         csRight = colorPickerLeft + colorSelectorX + 0.5F;
         csBottom = colorPickerTop + colorSelectorY + 0.5F;
         Render2DMethods.drawRect(context, csLeft - 1.0F, csTop - 1.0F, csLeft, csBottom + 1.0F, -16777216);
         Render2DMethods.drawRect(context, csRight, csTop - 1.0F, csRight + 1.0F, csBottom + 1.0F, -16777216);
         Render2DMethods.drawRect(context, csLeft, csTop - 1.0F, csRight, csTop, -16777216);
         Render2DMethods.drawRect(context, csLeft, csBottom, csRight, csBottom + 1.0F, -16777216);
         Render2DMethods.drawRect(context, csLeft, csTop, csRight, csBottom, selectorWhiteOverlayColor);
         float hueSliderLeft = colorPickerRight + 2.0F;
         float hueSliderRight = hueSliderLeft + 4.0F;
         int hueMouseY = (int)MathUtil.clamp((float)mouseY, colorPickerTop, colorPickerBottom);
         float hueSliderYDif = colorPickerBottom - colorPickerTop;
         float hueSelectorY = (1.0F - this.hue) * hueSliderYDif;
         float inc;
         if (this.hueSelectorDragging) {
            inc = (float)hueMouseY - colorPickerTop;
            this.hue = 1.0F - inc / hueSliderYDif;
            hueSelectorY = inc;
            this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness));
         }

         Render2DMethods.drawRect(context, hueSliderLeft - 0.5F, colorPickerTop - 0.5F, hueSliderRight + 0.5F, colorPickerBottom + 0.5F, -16777216);
         inc = 0.2F;
         float times = 5.0F;
         float sHeight = colorPickerBottom - colorPickerTop;
         float size = sHeight / 5.0F;
         float sY = colorPickerTop;

         for(int i = 0; (float)i < 5.0F; ++i) {
            boolean last = (float)i == 4.0F;
            Render2DMethods.drawGradientRect(context, hueSliderLeft, sY, hueSliderRight, sY + size, false, Color.HSBtoRGB(1.0F - 0.2F * (float)i, 1.0F, 1.0F), Color.HSBtoRGB(1.0F - 0.2F * (float)(i + 1), 1.0F, 1.0F));
            if (!last) {
               sY += size;
            }
         }

         float hsTop = colorPickerTop + hueSelectorY - 0.5F;
         float hsBottom = colorPickerTop + hueSelectorY + 0.5F;
         Render2DMethods.drawRect(context, hueSliderLeft - 1.0F, hsTop - 1.0F, hueSliderLeft, hsBottom + 1.0F, -16777216);
         Render2DMethods.drawRect(context, hueSliderRight, hsTop - 1.0F, hueSliderRight + 1.0F, hsBottom + 1.0F, -16777216);
         Render2DMethods.drawRect(context, hueSliderLeft, hsTop - 1.0F, hueSliderRight, hsTop, -16777216);
         Render2DMethods.drawRect(context, hueSliderLeft, hsBottom, hueSliderRight, hsBottom + 1.0F, -16777216);
         Render2DMethods.drawRect(context, hueSliderLeft, hsTop, hueSliderRight, hsBottom, selectorWhiteOverlayColor);
         float alphaSliderTop = colorPickerBottom + 2.0F;
         float alphaSliderBottom = this.renderAlpha ? alphaSliderTop + 4.0F : alphaSliderTop;
         if (this.renderAlpha) {
            int color = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
            int r = color >> 16 & 255;
            int g = color >> 8 & 255;
            int elementCodec = color & 255;
            float hsHeight = colorPickerRight - colorPickerLeft;
            float alphaSelectorX = this.alpha * hsHeight;
            float asLeft;
            if (this.alphaSelectorDragging) {
               asLeft = (float)colorMouseX - colorPickerLeft;
               this.alpha = asLeft / hsHeight;
               alphaSelectorX = asLeft;
               this.updateColor((new Color(r, g, elementCodec, (int)(this.alpha * 255.0F))).getRGB());
            }

            Render2DMethods.drawRect(context, colorPickerLeft - 0.5F, alphaSliderTop - 0.5F, colorPickerRight + 0.5F, alphaSliderBottom + 0.5F, -16777216);
            Render2DMethods.drawCheckeredBackground(context, colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);
            Render2DMethods.drawGradientRect(context, colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom, true, (new Color(r, g, elementCodec, 0)).getRGB(), (new Color(r, g, elementCodec, 255)).getRGB());
            asLeft = colorPickerLeft + alphaSelectorX - 0.5F;
            float asRight = colorPickerLeft + alphaSelectorX + 0.5F;
            Render2DMethods.drawRect(context, asLeft - 1.0F, alphaSliderTop, asRight + 1.0F, alphaSliderBottom, -16777216);
            Render2DMethods.drawRect(context, asLeft, alphaSliderTop, asRight, alphaSliderBottom, selectorWhiteOverlayColor);
         }

         if (this.getColorValue().isGlobalAllowed()) {
            boolean hoveredSync = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, (double)(this.getFinishedX() + 3.0F), (double)(alphaSliderBottom + 2.0F), (double)(this.getWidth() - 4.0F), 14.0D);
            if (this.getColorValue().isGlobal()) {
               Rectangle globalRect = this.commonRenderRectangle().copy();
               globalRect.setY(alphaSliderBottom + 3.0F).setHeight(alphaSliderBottom + 16.0F);
               Render2DMethods.drawRect(context, globalRect, hoveredSync ? this.getColor().darker().getRGB() : this.getColor().getRGB());
            }

            Managers.getTextManager().drawString((DrawContext)context, "Global", (double)((int)colorPickerLeft), (double)((int)(alphaSliderBottom + 6.0F)), -1);
         }
      }

      if (this.getColorValue().isGlobal()) {
         float[] hsb = Color.RGBtoHSB(this.getColorValue().getColor().getRed(), this.getColorValue().getColor().getGreen(), this.getColorValue().getColor().getBlue(), (float[])null);
         if (this.hue != hsb[0] || this.saturation != hsb[1] || this.brightness != hsb[2] || this.alpha != (float)this.getColorValue().getColor().getAlpha() / 255.0F) {
            this.hue = hsb[0];
            this.saturation = hsb[1];
            this.brightness = hsb[2];
            this.alpha = (float)this.getColorValue().getColor().getAlpha() / 255.0F;
         }
      }

   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0 || button == 1) {
         boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)(this.getFinishedX() + this.getWidth() - 25.0F), (double)(this.getFinishedY() + 3.0F), 15.0D, 8.0D);
         if (this.isColorExtended()) {
            float expandedX = this.getFinishedX() + 1.0F;
            float expandedY = this.getFinishedY() + 14.0F;
            float colorPickerLeft = expandedX + 4.0F;
            float colorPickerTop = expandedY + 1.0F;
            float colorPickerRight = colorPickerLeft + (this.getWidth() - 14.0F);
            float colorPickerBottom = colorPickerTop + 86.0F;
            float alphaSliderTop = colorPickerBottom + 2.0F;
            float alphaSliderBottom = this.renderAlpha ? alphaSliderTop + 4.0F : alphaSliderTop;
            float hueSliderLeft = colorPickerRight + 2.0F;
            boolean hoveredSync = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)this.getFinishedX(), (double)(alphaSliderBottom + 2.0F), (double)(this.getWidth() - 4.0F), 14.0D) && this.colorValue.isGlobalAllowed();
            if (hoveredSync && this.getColorValue().isGlobalAllowed()) {
               this.click();
               if (!this.getColorValue().isGlobal()) {
                  this.lastColor = this.getColorValue().getColor();
               }

               this.getColorValue().setGlobal(!this.getColorValue().isGlobal());
               if (!this.getColorValue().isGlobal() && this.lastColor != null) {
                  float[] colorArray = Color.RGBtoHSB(this.lastColor.getRed(), this.lastColor.getGreen(), this.lastColor.getBlue(), (float[])null);
                  this.updateFull(colorArray);
               } else {
                  Color color = this.getColorValue().getColor();
                  float[] colorArray = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
                  this.updateFull(colorArray);
               }
            }

            if (!this.getColorValue().isGlobal() && !hoveredSync) {
               if (Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)colorPickerLeft, (double)(colorPickerTop - 32.0F), (double)(this.getWidth() - 14.0F), (double)(this.getHeight() - 4.0F))) {
                  this.colorSelectorDragging = true;
               }

               if (Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)hueSliderLeft, (double)(colorPickerTop - 32.0F), 4.0D, (double)(this.getHeight() - 4.0F))) {
                  this.hueSelectorDragging = true;
               }
            }

            if (!hoveredSync && Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)colorPickerLeft, (double)alphaSliderTop, (double)(this.getWidth() - 20.0F), 4.0D)) {
               this.alphaSelectorDragging = true;
            }
         }

         if (hovered) {
            this.setColorExtended(!this.isColorExtended());
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0 || button == 1) {
         if (this.colorSelectorDragging) {
            this.colorSelectorDragging = false;
         }

         if (this.alphaSelectorDragging) {
            this.alphaSelectorDragging = false;
         }

         if (this.hueSelectorDragging) {
            this.hueSelectorDragging = false;
         }
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public int getHex() {
      return this.getHex(this.hue, this.saturation, this.brightness);
   }

   public int getHex(float hue, float saturation, float brightness) {
      return Color.HSBtoRGB(hue, saturation, brightness);
   }

   private void updateFull(float[] hsb) {
      this.hue = hsb[0];
      this.saturation = hsb[1];
      this.brightness = hsb[2];
      this.updateColor(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
   }

   public void updateColor(int hex) {
      this.getColorValue().setValue(new Color(hex >> 16 & 255, hex >> 8 & 255, hex & 255, (int)(this.alpha * 255.0F)));
   }

   private void drawColorPickerRect(DrawContext context, float left, float top, float right, float bottom) {
      int hueBasedColor = Color.HSBtoRGB(this.hue, 1.0F, 1.0F);
      Render2DMethods.drawGradientRect(context, left, top, right, bottom, true, -1, hueBasedColor);
      Render2DMethods.drawGradientRect(context, left, top, right, bottom, false, 0, -16777216);
   }

   public float getExtendedHeight() {
      float globalButtonOffset = this.getColorValue().isGlobalAllowed() ? 14.5F : 0.0F;
      float alphaSliderOffset = this.renderAlpha ? 2.0F : 0.0F;
      return 107.0F + alphaSliderOffset + globalButtonOffset;
   }

   
   public ColorValue getColorValue() {
      return this.colorValue;
   }

   
   public boolean isColorExtended() {
      return this.colorExtended;
   }

   
   public boolean isColorSelectorDragging() {
      return this.colorSelectorDragging;
   }

   
   public boolean isAlphaSelectorDragging() {
      return this.alphaSelectorDragging;
   }

   
   public boolean isHueSelectorDragging() {
      return this.hueSelectorDragging;
   }

   
   public float getHue() {
      return this.hue;
   }

   
   public float getSaturation() {
      return this.saturation;
   }

   
   public float getBrightness() {
      return this.brightness;
   }

   
   public float getAlpha() {
      return this.alpha;
   }

   
   public int getGoHomeLilNigga() {
      Objects.requireNonNull(this);
      return 154;
   }

   
   public Color getLastColor() {
      return this.lastColor;
   }

   
   public boolean isRenderAlpha() {
      return this.renderAlpha;
   }

   
   public void setColorExtended(boolean colorExtended) {
      this.colorExtended = colorExtended;
   }

   
   public void setColorSelectorDragging(boolean colorSelectorDragging) {
      this.colorSelectorDragging = colorSelectorDragging;
   }

   
   public void setAlphaSelectorDragging(boolean alphaSelectorDragging) {
      this.alphaSelectorDragging = alphaSelectorDragging;
   }

   
   public void setHueSelectorDragging(boolean hueSelectorDragging) {
      this.hueSelectorDragging = hueSelectorDragging;
   }

   
   public void setHue(float hue) {
      this.hue = hue;
   }

   
   public void setSaturation(float saturation) {
      this.saturation = saturation;
   }

   
   public void setBrightness(float brightness) {
      this.brightness = brightness;
   }

   
   public void setAlpha(float alpha) {
      this.alpha = alpha;
   }

   
   public void setLastColor(Color lastColor) {
      this.lastColor = lastColor;
   }

   
   public void setRenderAlpha(boolean renderAlpha) {
      this.renderAlpha = renderAlpha;
   }
}
