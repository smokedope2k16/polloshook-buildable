package me.pollos.polloshook.impl.module.other.colours.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.impl.gui.click.component.values.ColorComponent;
import me.pollos.polloshook.impl.gui.click.component.values.NumberComponent;
import net.minecraft.client.gui.DrawContext;

public class SettingColorComponent extends ColorComponent {
   private final NumberValue<Float> hue = (new NumberValue<Float>(0.0F, 0.0F, 1.0F, 0.001F, new String[]{"Hue", "h"}) {
      public Float getValue() {
         return (Float)super.getValue();
      }

      public void setValue(Float value) {
         super.setValue((Float)value);
         SettingColorComponent.this.setHue(value);
         SettingColorComponent.this.updateColor(Color.HSBtoRGB(SettingColorComponent.this.getHue(), SettingColorComponent.this.getSaturation(), SettingColorComponent.this.getBrightness()));
      }
   }).withTag("colorsetting_hue");
   private final NumberValue<Float> saturation = (new NumberValue<Float>(1.0F, 0.0F, 1.0F, 0.01F, new String[]{"Saturation", "s"}) {
      public Float getValue() {
         return (Float)super.getValue();
      }

      public void setValue(Float value) {
         super.setValue((Float)value);
         SettingColorComponent.this.setSaturation(value);
         SettingColorComponent.this.updateColor(Color.HSBtoRGB(SettingColorComponent.this.getHue(), SettingColorComponent.this.getSaturation(), SettingColorComponent.this.getBrightness()));
      }
   }).withTag("colorsetting_saturation");
   private final NumberValue<Float> brightness = (new NumberValue<Float>(0.45F, 0.0F, 1.0F, 0.01F, new String[]{"Brightness", "elementCodec"}) {
      public Float getValue() {
         return (Float)super.getValue();
      }

      public void setValue(Float value) {
         super.setValue((Float)value);
         SettingColorComponent.this.setBrightness(value);
         SettingColorComponent.this.updateColor(Color.HSBtoRGB(SettingColorComponent.this.getHue(), SettingColorComponent.this.getSaturation(), SettingColorComponent.this.getBrightness()));
      }
   }).withTag("colorsetting_lightness");
   private final List<NumberComponent> hslSettings = new ArrayList();
   private final Rectangle rect;

   public SettingColorComponent(ColorValue colorValue, Rectangle rect, float offsetX, float offsetY) {
      super(colorValue, rect, offsetX, offsetY);
      if (colorValue.getLabel().equalsIgnoreCase("Color")) {
         colorValue.setGlobalAllowed(false);
      }

      this.setRenderAlpha(false);
      this.rect = rect;
   }

   public void setColorExtended(boolean extended) {
      super.setColorExtended(extended);
      this.initSettings();
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
      this.hslSettings.forEach((c) -> {
         c.moved(this.getFinishedX(), this.getFinishedY());
      });
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      if (this.isColorExtended()) {
         this.hslSettings.forEach((c) -> {
            c.renderNumberButton(context, mouseX, mouseY);
         });
      }

      this.hue.setValue((Float)this.getHue());
      this.saturation.setValue((Float)this.getSaturation());
      this.brightness.setValue((Float)this.getBrightness());
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean handled = this.hslSettings.stream().anyMatch((component) -> {
         return component.mouseClicked(mouseX, mouseY, button) && component.hoveringCommonRectangle((int)mouseX, (int)mouseY);
      }) && !this.getColorValue().isGlobal();
      return handled || super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      boolean handled = this.hslSettings.stream().anyMatch((component) -> {
         return component.mouseReleased(mouseX, mouseY, button) && component.hoveringCommonRectangle((int)mouseX, (int)mouseY);
      }) && !this.getColorValue().isGlobal();
      return handled || super.mouseReleased(mouseX, mouseY, button);
   }

   public void setHeight(float height) {
      float bro = this.getColorValue().isGlobalAllowed() ? 0.0F : 0.5F;
      super.setHeight(this.isColorExtended() ? this.getExtendedHeight() + 40.0F + bro : 14.0F);
   }

   float offY;

   private void initSettings() {
      this.hslSettings.clear();
      offY = 105.5F + (float)(this.getColorValue().isGlobalAllowed() ? 14 : 0);
      /* 
      this.register(new NumberComponent(this.hue) {
        {
            this.moved(SettingColorComponent.this.getFinishedX(), SettingColorComponent.this.getFinishedY() + offY);
        }

        @Override
        public void setSettingFromX(int mouseX) {
            if (!SettingColorComponent.this.getColorValue().isGlobal()) {
                super.setSettingFromX(mouseX);
            }
        }
    });

    offY += 14.0F;

    this.register(new NumberComponent(this.saturation) {
        {
            this.moved(SettingColorComponent.this.getFinishedX(), SettingColorComponent.this.getFinishedY() + offY);
        }

        @Override
        public void setSettingFromX(int mouseX) {
            if (!SettingColorComponent.this.getColorValue().isGlobal()) {
                super.setSettingFromX(mouseX);
            }
        }
    });

    offY += 14.0F;

    this.register(new NumberComponent(this.brightness) {
        {
            this.moved(SettingColorComponent.this.getFinishedX(), SettingColorComponent.this.getFinishedY() + offY);
        }

        @Override
        public void setSettingFromX(int mouseX) {
            if (!SettingColorComponent.this.getColorValue().isGlobal()) {
                super.setSettingFromX(mouseX);
            }
        }
    });
   }

   private void register(NumberComponent component) {
      boolean exists = this.hslSettings.stream().anyMatch((c) -> {
         return c.getNumberValue().getLabel().equals(component.getNumberValue().getLabel());
      });
      if (!exists) {
         this.hslSettings.add(component);
      }
      */ 
      //TODO:  Fix this fucking implemetation
   }
}
