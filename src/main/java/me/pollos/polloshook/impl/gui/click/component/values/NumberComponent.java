package me.pollos.polloshook.impl.gui.click.component.values;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.math.RoundingUtil;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import me.pollos.polloshook.impl.module.other.clickgui.mode.NumberStyle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public class NumberComponent extends ValueComponent<Number, NumberValue<Number>> {
   private static NumberValue<Number> numberValue;
   private boolean sliding;
   private float customFontFix = 0.0F;

   public NumberComponent(NumberValue<Number> numberValue, Rectangle rect, float offsetX, float offsetY) {
      super(numberValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), numberValue);
      this.numberValue = numberValue;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      this.renderNumberButton(context, mouseX, mouseY);
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, (double)(this.getFinishedX() + 1.0F), (double)(this.getFinishedY() + 1.0F), (double)(this.getWidth() - 2.0F), (double)(this.getHeight() - 2.0F));
      if (hovered && button == 0) {
         this.setSliding(true);
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (this.isSliding()) {
         this.setSliding(false);
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public void renderNumberButton(DrawContext context, int mouseX, int mouseY) {
      this.customFontFix = 0.0F;
      if (Managers.getTextManager().isCustom()) {
         this.customFontFix = 2.0F;
      }

      boolean hovered = this.hoveringCommonRectangle(mouseX, mouseY);
      float length = (float)MathHelper.floor((((Number)this.getNumberValue().getValue()).floatValue() - this.getNumberValue().getMinimum().floatValue()) / (this.getNumberValue().getMaximum().floatValue() - this.getNumberValue().getMinimum().floatValue()) * (this.getWidth() - 2.0F));
      float xOffset = this.getFinishedX() + 2.5F;
      Rectangle common = new Rectangle(xOffset, this.getFinishedY() + 12.5F, this.getFinishedX() + this.getWidth() - 2.0F, this.getFinishedY() + this.getHeight() - 0.5F);
      if (this.numberValue.isNoLimit()) {
         Render2DMethods.scissor((float)((int)this.getPosX()), (float)((int)(this.getPosY() + this.getHeight())), (float)((int)(this.getPosX() + this.getWidth())), (float)((int)(this.getPosY() + this.getHeight() + (float)context.getScaledWindowHeight())));
      }

      switch((NumberStyle)ClickGUI.get().getNumberButtonStyle().getValue()) {
      case FAT:
         Rectangle fatRect = common.copy().setY(this.getFinishedY() + 1.0F).setWidth(Math.max(this.getFinishedX() + length, this.getFinishedX() + this.customFontFix)).setHeight(this.getFinishedY() + this.getHeight() - 0.5F);
         Render2DMethods.drawRect(context, fatRect, hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
         break;
      case TWINK:
         Render2DMethods.drawRect(context, common.copy().setWidth(Math.max(common.getWidth(), this.getFinishedX() + this.customFontFix)), hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
         Color lengthColor = this.getColor(95);
         Render2DMethods.drawRect(context, common.copy().setWidth(Math.max(this.getFinishedX() + length, this.getFinishedX() + this.customFontFix)), hovered ? lengthColor.darker().getRGB() : lengthColor.getRGB());
      }

      if (this.numberValue.isNoLimit()) {
         RenderSystem.disableScissor();
      }

      Managers.getTextManager().drawString((DrawContext)context, this.getLabel() + ": " + formatUsingTag((NumberValue)this.value), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
      if (this.sliding) {
         this.setSettingFromX(mouseX);
      }

   }

   public void setSettingFromX(int mouseX) {
      float difference = this.getNumberValue().getMaximum().floatValue() - this.getNumberValue().getMinimum().floatValue();
      float percent = ((float)mouseX - this.getFinishedX()) / (this.getWidth() - 2.0F);
      Number minimum = this.getNumberValue().getMinimum();
      Number maximum = this.getNumberValue().getMaximum();
      Number currentValue = (Number)this.getNumberValue().getValue();
      Number steps = this.getNumberValue().getSteps() == null ? 1 : this.getNumberValue().getSteps();
      if (currentValue instanceof Double) {
         double result = minimum.doubleValue() + (double)(difference * percent);
         double rounded = RoundingUtil.roundToStep(result, ((Number)steps).doubleValue());
         this.getNumberValue().setValue((Number)MathHelper.clamp(rounded, minimum.doubleValue(), maximum.doubleValue()));
      } else if (currentValue instanceof Float) {
         float result = minimum.floatValue() + difference * percent;
         float rounded = RoundingUtil.roundToStep(result, ((Number)steps).floatValue());
         this.getNumberValue().setValue((Number)MathHelper.clamp(rounded, minimum.floatValue(), maximum.floatValue()));
      } else if (currentValue instanceof Integer) {
         int result = (int)((float)minimum.intValue() + difference * percent);
         int rounded = RoundingUtil.roundToStep(result, ((Number)steps).intValue());
         this.getNumberValue().setValue((Number)MathHelper.clamp(rounded, minimum.intValue(), maximum.intValue()));
      } else if (currentValue instanceof Long) {
         long result = (long)((float)minimum.longValue() + difference * percent);
         long rounded = RoundingUtil.roundToStep(result, ((Number)steps).longValue());
         this.getNumberValue().setValue((Number)MathHelper.clamp(rounded, minimum.longValue(), maximum.longValue()));
      }

   }

   public static String formatUsingTag(NumberValue<?> value) {
      int intVal = ((Number)value.getValue()).intValue();
      float floatVal = ((Number)value.getValue()).floatValue();
      String tag = value.getTag();
      int common = 2;
      if (tag != null) {
         String var5 = tag.toLowerCase();
         byte var6 = -1;
         switch(var5.hashCode()) {
         case -1335595316:
            if (var5.equals("degree")) {
               var6 = 8;
            }
            break;
         case -906279820:
            if (var5.equals("second")) {
               var6 = 4;
            }
            break;
         case -726766140:
            if (var5.equals("colorsetting_saturation")) {
               var6 = 10;
            }
            break;
         case -514291133:
            if (var5.equals("holefill")) {
               var6 = 7;
            }
            break;
         case -221921451:
            if (var5.equals("timechanger")) {
               var6 = 1;
            }
            break;
         case 37:
            if (var5.equals("%")) {
               var6 = 3;
            }
            break;
         case 3494:
            if (var5.equals("ms")) {
               var6 = 5;
            }
            break;
         case 3293947:
            if (var5.equals("km/h")) {
               var6 = 2;
            }
            break;
         case 108280125:
            if (var5.equals("range")) {
               var6 = 9;
            }
            break;
         case 254790715:
            if (var5.equals("colorsetting_lightness")) {
               var6 = 11;
            }
            break;
         case 1271172934:
            if (var5.equals("colorsetting_hue")) {
               var6 = 12;
            }
            break;
         case 1403048670:
            if (var5.equals("thousand")) {
               var6 = 6;
            }
            break;
         case 1909617316:
            if (var5.equals("fovmodifier")) {
               var6 = 0;
            }
         }

         switch(var6) {
         case 0:
            if (((Number)value.getValue()).floatValue() <= value.getMinimum().floatValue()) {
               return "Off";
            }

            return ((Number)value.getValue()).intValue() + "%";
         case 1:
            float time = ((Number)value.getValue()).floatValue() / 24000.0F * 24.0F;
            int hours = (int)time;
            int minutes = (int)((time - (float)hours) * 60.0F);
            int seconds = (int)(((time - (float)hours) * 60.0F - (float)minutes) * 60.0F);
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
         case 2:
            return String.format("%.2fkm/h", floatVal);
         case 3:
            return String.format("%." + common + "f%%", floatVal);
         case 4:
            return String.format("%.1fs", floatVal);
         case 5:
            return String.format("%dms", intVal);
         case 6:
            return String.format("%.1fk", floatVal);
         case 7:
            if (intVal >= value.getMaximum().intValue()) {
               return "Infinite";
            }
            break;
         case 8:
            return String.format("%." + common + "f%s", floatVal, "°");
         case 9:
            return String.format("%." + common + "fm", floatVal);
         case 10:
         case 11:
            return String.format("%.2f", floatVal * 100.0F);
         case 12:
            return String.format("%.2f", floatVal * 360.0F);
         }
      }

      return String.format("%." + common + "f", ((Number)value.getValue()).floatValue());
   }


   public NumberValue<Number> getNumberValue() {
      return this.numberValue;
   }


   public boolean isSliding() {
      return this.sliding;
   }


   public float getCustomFontFix() {
      return this.customFontFix;
   }


   public void setSliding(boolean sliding) {
      this.sliding = sliding;
   }


   public void setCustomFontFix(float customFontFix) {
      this.customFontFix = customFontFix;
   }
}
