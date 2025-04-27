package me.pollos.polloshook.api.value.value;

import java.awt.Color;
import java.util.function.Supplier;

import me.pollos.polloshook.api.interfaces.Minecraftable;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.Module;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.api.value.value.parents.impl.Parent;
import me.pollos.polloshook.impl.manager.internal.CommandManager;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.other.colours.util.HSLColor;
import net.minecraft.util.Formatting;

public class ColorValue extends Value<Color> implements Minecraftable {
   private boolean global;
   private boolean globalAllowed = true;
   private boolean renderAlpha = true;

   public ColorValue(Color color, boolean global, String... names) {
      super(color, names);
      this.value = color;
      this.global = global;
   }

   public ColorValue setParent(Parent parent) {
      this.parent = parent;
      return this;
   }

   public ColorValue setParent(Value<Boolean> parent) {
      super.setParent(parent);
      return this;
   }

   public ColorValue setParent(Value<Boolean> parent, boolean opposite) {
      super.setParent(parent, opposite);
      return this;
   }

   public ColorValue setParent(EnumValue<?> parent, Enum<?> target) {
      super.setParent(parent, target);
      return this;
   }

   public ColorValue setParent(EnumValue<?> parent, Enum<?> target, boolean opposite) {
      super.setParent(parent, target, opposite);
      return this;
   }

   public ColorValue setParent(Supplier<Boolean> parent) {
      super.setParent(parent);
      return this;
   }

   public Color getColor() {
      return this.isGlobal() && this.globalAllowed ? Colours.get().getColourCustomAlpha(((Color)this.value).getAlpha()) : (Color)this.value;
   }

   public String returnValue(String[] args) {
      Module mod = Managers.getCommandManager().getModule(args);
      int red = ((Color)this.getValue()).getRed();
      int green = ((Color)this.getValue()).getGreen();
      int blue = ((Color)this.getValue()).getBlue();
      int alpha = ((Color)this.getValue()).getAlpha();
      boolean isGlobal = this.isGlobal();
      if (args.length > 2) {
         try {
            String var8 = args[2].toUpperCase();
            byte var9 = -1;
            switch(var8.hashCode()) {
            case -1884956477:
               if (var8.equals("RANDOM")) {
                  var9 = 8;
               }
               break;
            case 65:
               if (var8.equals("A")) {
                  var9 = 6;
               }
               break;
            case 66:
               if (var8.equals("B")) {
                  var9 = 4;
               }
               break;
            case 67:
               if (var8.equals("C")) {
                  var9 = 14;
               }
               break;
            case 71:
               if (var8.equals("G")) {
                  var9 = 2;
               }
               break;
            case 80:
               if (var8.equals("P")) {
                  var9 = 16;
               }
               break;
            case 82:
               if (var8.equals("R")) {
                  var9 = 0;
               }
               break;
            case 83:
               if (var8.equals("S")) {
                  var9 = 10;
               }
               break;
            case 81009:
               if (var8.equals("RED")) {
                  var9 = 1;
               }
               break;
            case 81288:
               if (var8.equals("RND")) {
                  var9 = 9;
               }
               break;
            case 81986:
               if (var8.equals("SET")) {
                  var9 = 11;
               }
               break;
            case 2041946:
               if (var8.equals("BLUE")) {
                  var9 = 5;
               }
               break;
            case 2074485:
               if (var8.equals("COPY")) {
                  var9 = 15;
               }
               break;
            case 2560667:
               if (var8.equals("SYNC")) {
                  var9 = 12;
               }
               break;
            case 62372158:
               if (var8.equals("ALPHA")) {
                  var9 = 7;
               }
               break;
            case 68081379:
               if (var8.equals("GREEN")) {
                  var9 = 3;
               }
               break;
            case 75900531:
               if (var8.equals("PASTE")) {
                  var9 = 17;
               }
               break;
            case 2105276323:
               if (var8.equals("GLOBAL")) {
                  var9 = 13;
               }
            }

            String hex;
            int keyCodec;
            int r;
            int g;
            int redValue;
            switch(var9) {
            case 0:
            case 1:
               redValue = Integer.parseInt(args[3]);
               this.setValue(new Color(redValue, green, blue, alpha));
               CommandManager.setColorMessage(mod, this, "red", redValue);
               break;
            case 2:
            case 3:
               redValue = Integer.parseInt(args[3]);
               this.setValue(new Color(red, redValue, blue, alpha));
               return CommandManager.setColorMessage(mod, this, "green", redValue);
            case 4:
            case 5:
               redValue = Integer.parseInt(args[3]);
               this.setValue(new Color(red, green, redValue, alpha));
               return CommandManager.setColorMessage(mod, this, "blue", redValue);
            case 6:
            case 7:
               redValue = Integer.parseInt(args[3]);
               this.setValue(new Color(red, green, blue, redValue));
               return CommandManager.setColorMessage(mod, this, "alpha", redValue);
            case 8:
            case 9:
               this.setValue(new Color(mc.world.random.nextInt(255), mc.world.random.nextInt(255), mc.world.random.nextInt(255)));
               hex = String.format("Red: %s Green: %s Blue %s", ((Color)this.getValue()).getRed(), ((Color)this.getValue()).getGreen(), ((Color)this.getValue()).getBlue());
               return CommandManager.setMessage(mod, this, hex);
            case 10:
            case 11:
               redValue = Integer.parseInt(args[3]);
               keyCodec = Integer.parseInt(args[4]);
               r = Integer.parseInt(args[5]);
               if (args.length == 6) {
                  this.setValue(new Color(redValue, keyCodec, r, alpha));
                  String colorStr = String.format("Red: %s Green: %s Blue: %s", redValue, keyCodec, r);
                  return CommandManager.setMessage(mod, this, colorStr);
               }

               g = Integer.parseInt(args[6]);
               this.setValue(new Color(redValue, keyCodec, r, g));
               String colorStr = String.format("Red: %s Green: %s Blue: %s Alpha: %s", redValue, keyCodec, r, g);
               return CommandManager.setMessage(mod, this, colorStr);
            case 12:
            case 13:
               this.setGlobal(!isGlobal);
               return String.format("%s%s%s property %s%s%s Global was %s", Formatting.YELLOW, mod.getLabel(), Formatting.GRAY, Formatting.AQUA, this.getLabel(), Formatting.GRAY, !isGlobal ? String.valueOf(Formatting.GREEN) + "enabled" : String.valueOf(Formatting.RED) + "disabled");
            case 14:
            case 15:
               hex = String.format("#%02x%02x%02x%02x", alpha, red, green, blue);
               mc.keyboard.setClipboard(hex);
               return "Copied colour to clipboard";
            case 16:
            case 17:
               try {
                  if (mc.keyboard.getClipboard() != null) {
                     if (mc.keyboard.getClipboard().startsWith("#")) {
                        hex = mc.keyboard.getClipboard();
                        keyCodec = Integer.valueOf(hex.substring(1, 3), 16);
                        r = Integer.valueOf(hex.substring(3, 5), 16);
                        g = Integer.valueOf(hex.substring(5, 7), 16);
                        int elementCodec = Integer.valueOf(hex.substring(7, 9), 16);
                        this.setValue(new Color(r, g, elementCodec, keyCodec));
                        this.setValue(new Color(r, g, elementCodec, keyCodec));
                     } else {
                        String[] color = mc.keyboard.getClipboard().split(",");
                        Color colorValue = new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]));
                        this.setValue(colorValue);
                        this.setValue(colorValue);
                     }

                     return String.format("Colour pasted in property %s", this.getLabel());
                  }
               } catch (NumberFormatException var15) {
                  return "Bad colour format";
               }
            }
         } catch (Exception var16) {
            return "Invalid action";
         }
      }

      float[] hsl = HSLColor.fromRGB(this.getColor());
      String hex = ColorUtil.colorToHex(this.getColor());
      return String.format("%s %s(RGBA: Red %s, Green %s, Blue %s, Alpha %s\nHSL: Hue %.1f, Saturation %.1f, Lightness %.1f\nHEX: #%s)%s [Global = %s]", this.getLabel(), Formatting.GREEN, red, green, blue, alpha, hsl[0], hsl[1], hsl[2], hex.toUpperCase(), Formatting.GRAY, isGlobal);
   }

   
   public ColorValue setGlobal(boolean global) {
      this.global = global;
      return this;
   }

   
   public ColorValue setGlobalAllowed(boolean globalAllowed) {
      this.globalAllowed = globalAllowed;
      return this;
   }

   
   public ColorValue setRenderAlpha(boolean renderAlpha) {
      this.renderAlpha = renderAlpha;
      return this;
   }

   
   public boolean isGlobal() {
      return this.global;
   }

   
   public boolean isGlobalAllowed() {
      return this.globalAllowed;
   }

   
   public boolean isRenderAlpha() {
      return this.renderAlpha;
   }
}