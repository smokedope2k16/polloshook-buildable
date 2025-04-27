package me.pollos.polloshook.api.util.color;

import java.awt.Color;
import me.pollos.polloshook.impl.module.other.colours.util.HSLColor;
import net.minecraft.util.math.MathHelper;

public class ColorUtil {
   public static Color changeAlpha(Color origColor, int alpha) {
      return new Color(origColor.getRed(), origColor.getGreen(), origColor.getBlue(), alpha);
   }

   public static Color rainbow(float speed, float off, float saturation, float lightness) {
      return rainbow(speed, off, saturation, lightness, false);
   }

   public static Color rainbow(float speed, float off, float saturation, float lightness, boolean up) {
      double time = (double)System.currentTimeMillis() * (double)(speed / 20.0F);
      if (up) {
         off -= 5.0F;
         time -= (double)off;
      } else {
         time += (double)off;
      }

      time %= 360.0D;
      return HSLColor.toRGB((float)time, saturation, lightness);
   }

   public static String colorToHex(Color color) {
      String hex;
      if (color.getAlpha() == 255) {
         hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
      } else {
         hex = String.format("#%02x%02x%02x%02x", color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
      }

      return hex.replace("#", "");
   }

   public static Color hexToColor(String hexStr) {
      if (!hexStr.startsWith("#")) {
         hexStr = "#" + hexStr;
      }

      Color color;
      int keyCodec;
      int r;
      int g;
      if (hexStr.length() == 9) {
         keyCodec = Integer.valueOf(hexStr.substring(1, 3), 16);
         r = Integer.valueOf(hexStr.substring(3, 5), 16);
         g = Integer.valueOf(hexStr.substring(5, 7), 16);
         int elementCodec = Integer.valueOf(hexStr.substring(7, 9), 16);
         color = new Color(r, g, elementCodec, keyCodec);
      } else {
         keyCodec = Integer.valueOf(hexStr.substring(1, 3), 16);
         r = Integer.valueOf(hexStr.substring(3, 5), 16);
         g = Integer.valueOf(hexStr.substring(5, 7), 16);
         color = new Color(keyCodec, r, g);
      }

      return color;
   }

   public static Color twoColorGradient(float speed, float off, boolean up, Color colorOne, Color colorTwo) {
      double time = (double)System.currentTimeMillis() * (double)(speed / 20.0F);
      if (up) {
         time += (double)off;
      } else {
         time -= (double)off;
      }

      double timer = time / (850.0D / (double)speed) % 1.0D;
      double abs = Math.abs(timer - 0.5D);
      float ratio = (float)abs * 2.0F;
      return blendColors(colorOne, colorTwo, ratio);
   }

   public static Color threeColorGradient(float speed, float off, boolean up, Color colorOne, Color colorTwo, Color colorThree) {
      double time = (double)System.currentTimeMillis() * (double)(speed / 20.0F);
      if (up) {
         time += (double)off;
      } else {
         time -= (double)off;
      }

      double timer = time / (850.0D / (double)speed) % 1.0D;
      double abs = Math.abs(timer - 0.5D);
      float ratio = (float)abs * 2.0F;
      Color blendedColor;
      if (ratio <= 0.5F) {
         blendedColor = blendColors(colorOne, colorTwo, ratio * 2.0F);
      } else {
         blendedColor = blendColors(colorTwo, colorThree, (ratio - 0.5F) * 2.0F);
      }

      return blendedColor;
   }

   public static Color fourColorGradient(float speed, float off, boolean up, Color colorOne, Color colorTwo, Color colorThree, Color colorFour) {
      double time = (double)System.currentTimeMillis() * (double)(speed / 20.0F);
      if (up) {
         time += (double)off;
      } else {
         time -= (double)off;
      }

      double timer = time / (850.0D / (double)speed) % 1.0D;
      Color blendedColor;
      float ratio;
      if (timer <= 0.25D) {
         ratio = (float)(timer / 0.25D);
         blendedColor = blendColors(colorOne, colorTwo, ratio);
      } else if (timer <= 0.5D) {
         ratio = (float)((timer - 0.25D) / 0.25D);
         blendedColor = blendColors(colorTwo, colorThree, ratio);
      } else if (timer <= 0.75D) {
         ratio = (float)((timer - 0.5D) / 0.25D);
         blendedColor = blendColors(colorThree, colorFour, ratio);
      } else {
         ratio = (float)((timer - 0.75D) / 0.25D);
         blendedColor = blendColors(colorFour, colorOne, ratio);
      }

      return blendedColor;
   }

   private static Color blendColors(Color color1, Color color2, float ratio) {
      int red = (int)((float)color1.getRed() * (1.0F - ratio) + (float)color2.getRed() * ratio);
      int green = (int)((float)color1.getGreen() * (1.0F - ratio) + (float)color2.getGreen() * ratio);
      int blue = (int)((float)color1.getBlue() * (1.0F - ratio) + (float)color2.getBlue() * ratio);
      return new Color(fixColor(red), fixColor(green), fixColor(blue));
   }

   public static int fixColor(int value) {
      return Math.max(0, Math.min(255, value));
   }

   public static double fade(double value, double max) {
      double fadeAmount = normalize((double)System.currentTimeMillis() - value, max);
      int alpha = (int)(fadeAmount * 255.0D);
      alpha = MathHelper.clamp(alpha, 0, 255);
      alpha = 255 - alpha;
      return (double)alpha;
   }

   private static double normalize(double value, double max) {
      return (value - 0.0D) / (max - 0.0D);
   }

   public static Color toColor(float red, float green, float blue, float alpha) {
      if (!(green < 0.0F) && !(green > 100.0F)) {
         if (!(blue < 0.0F) && !(blue > 100.0F)) {
            if (!(alpha < 0.0F) && !(alpha > 1.0F)) {
               red = red % 360.0F / 360.0F;
               green /= 100.0F;
               blue /= 100.0F;
               float blueOff;
               if ((double)blue < 0.0D) {
                  blueOff = blue * (1.0F + green);
               } else {
                  blueOff = blue + green - green * blue;
               }

               green = 2.0F * blue - blueOff;
               blue = Math.max(0.0F, getFactor(green, blueOff, red + 0.33333334F));
               float max = Math.max(0.0F, getFactor(green, blueOff, red));
               green = Math.max(0.0F, getFactor(green, blueOff, red - 0.33333334F));
               blue = Math.min(blue, 1.0F);
               max = Math.min(max, 1.0F);
               green = Math.min(green, 1.0F);
               return new Color(blue, max, green, alpha);
            } else {
               throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
            }
         } else {
            throw new IllegalArgumentException("Color parameter outside of expected range - Lightness");
         }
      } else {
         throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
      }
   }

   public static float getFactor(float red, float green, float blue) {
      if (blue < 0.0F) {
         ++blue;
      }

      if (blue > 1.0F) {
         --blue;
      }

      if (6.0F * blue < 1.0F) {
         return red + (green - red) * 6.0F * blue;
      } else if (2.0F * blue < 1.0F) {
         return green;
      } else {
         return 3.0F * blue < 2.0F ? red + (green - red) * 6.0F * (0.6666667F - blue) : red;
      }
   }

   public static Color blend(Color... c) {
      float ratio = 1.0F / (float)c.length;
      int keyCodec = 0;
      int r = 0;
      int g = 0;
      int elementCodec = 0;
      Color[] var6 = c;
      int var7 = c.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Color color = var6[var8];
         int rgb = color.getRGB();
         int a1 = rgb >> 24 & 255;
         int r1 = (rgb & 16711680) >> 16;
         int g1 = (rgb & '\uff00') >> 8;
         int b1 = rgb & 255;
         keyCodec += (int)((float)a1 * ratio);
         r += (int)((float)r1 * ratio);
         g += (int)((float)g1 * ratio);
         elementCodec += (int)((float)b1 * ratio);
      }

      Color color = new Color(keyCodec << 24 | r << 16 | g << 8 | elementCodec);
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), c[0].getAlpha());
   }
}
