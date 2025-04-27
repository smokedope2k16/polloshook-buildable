package me.pollos.polloshook.api.module.hud.interfaces;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.text.FormattingString;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.other.hud.HUD;
import net.minecraft.client.gui.DrawContext;

public interface Element2D {
   void draw(DrawContext var1);

   default void drawText(DrawContext context, String text, int x, int y) {
      this.drawText(context, text, x, y, this.getColor(y).getRGB(), false);
   }

   default void drawText(DrawContext context, String text, int x, int y, int color) {
      this.drawText(context, text, x, y, color, false);
   }

   default void drawText(DrawContext context, String text, int x, int y, int color, boolean forceColor) {
      int offX = x;
      HUD HUD_MODULE = HUD.get();
      List<FormattingString> stringList = FormattingString.fromString(text);
      boolean horizontal = (Boolean)HUD_MODULE.getHorizontal().getValue();
      FormattingString string;
      if (horizontal && !forceColor) {
         for(Iterator var11 = stringList.iterator(); var11.hasNext(); offX += (int)this.getWidth(string.str())) {
            string = (FormattingString)var11.next();
            Managers.getTextManager().drawString(context, string.str(), (double)offX, (double)y, this.getColor(offX).getRGB());
         }
      } else {
         Managers.getTextManager().drawString(context, text, (double)x, (double)y, forceColor ? color : this.getColor(y).getRGB());
      }

   }

   default float getWidth(String s) {
      return Managers.getTextManager().getWidth(s);
   }

   default int getHeight() {
      return Managers.getTextManager().getHeight();
   }

   default Color getColor(int off) {
      HUD HUD_MODULE = HUD.get();
      Value<Boolean> horizontal = HUD_MODULE.getHorizontal();
      Value<Boolean> up = HUD_MODULE.getDown();
      Value<Boolean> left = HUD_MODULE.getLeft();
      Value<Boolean> gradient = HUD_MODULE.getGradient();
      Value<Boolean> rainbow = HUD_MODULE.getRainbow();
      NumberValue<Float> rainbowOffset = HUD_MODULE.getRainbowOffset();
      NumberValue<Float> rainbowSpeed = HUD_MODULE.getRainbowSpeed();
      NumberValue<Integer> gradientsSize = HUD_MODULE.getGradients();
      ColorValue colorOne = HUD_MODULE.getColorOne();
      ColorValue colorTwo = HUD_MODULE.getColorTwo();
      ColorValue colorThree = HUD_MODULE.getColorThree();
      ColorValue colorFour = HUD_MODULE.getColorFour();
      float offset = (Boolean)gradient.getValue() ? (Float)rainbowOffset.getValue() / 2.0F : (Float)rainbowOffset.getValue();
      if ((Boolean)rainbow.getValue()) {
         float yOffset = (float)off / offset;
         boolean direction = (Boolean)horizontal.getValue() ? !(Boolean)left.getValue() : !(Boolean)up.getValue();
         if ((Boolean)gradient.getValue()) {
            switch((Integer)gradientsSize.getValue()) {
            case 2:
               return ColorUtil.twoColorGradient((Float)rainbowSpeed.getValue(), yOffset, direction, colorOne.getColor(), colorTwo.getColor());
            case 3:
               return ColorUtil.threeColorGradient((Float)rainbowSpeed.getValue(), yOffset, direction, colorOne.getColor(), colorTwo.getColor(), colorThree.getColor());
            case 4:
               return ColorUtil.fourColorGradient((Float)rainbowSpeed.getValue(), yOffset, direction, colorOne.getColor(), colorTwo.getColor(), colorThree.getColor(), colorFour.getColor());
            }
         }

         return ColorUtil.rainbow((Float)rainbowSpeed.getValue(), yOffset, (Float)Colours.get().getRainbowSaturation().getValue(), (Float)Colours.get().getRainbowLightness().getValue());
      } else {
         return colorOne.getColor();
      }
   }
}
