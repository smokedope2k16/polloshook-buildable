package me.pollos.polloshook.impl.module.other.hud.elements.consistent.coords;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.util.math.MathUtil;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class Coords extends HUDModule {
   private final Value<Boolean> baseProtection = new Value(false, new String[]{"BaseProtection", "screenshare"});
   private final NumberValue<Integer> hideAfter;
   private final EnumValue<DirectionMode> mode;
   private final Value<Boolean> nether;
   private final Value<Boolean> rotations;
   private final Value<Boolean> server;
   private final Value<Boolean> sameLine;
   private final Value<Boolean> color;
   private final Value<Boolean> yaw;

   public Coords() {
      super(new String[]{"Coordinates", "coords"});
      this.hideAfter = (new NumberValue(100, 50, 250, new String[]{"HideAfter", "hidepast"})).setParent(this.baseProtection).withTag("thousand");
      this.mode = new EnumValue(DirectionMode.BOTTOM, new String[]{"Direction", "dir"});
      this.nether = new Value(true, new String[]{"Nether", "nethercoordinates"});
      this.rotations = (new Value(true, new String[]{"Rotations", "rotate"})).setParent(this.mode, DirectionMode.ABOVE, true);
      this.server = (new Value(false, new String[]{"DebugRotations", "server"})).setParent(this.rotations);
      this.sameLine = (new Value(false, new String[]{"SameLine", "1line"})).setParent(this.rotations);
      this.color = (new Value(false, new String[]{"ColorText", "colortxt"})).setParent(this.mode, DirectionMode.ABOVE);
      this.yaw = (new Value(false, new String[]{"DisplayYaw", "yaw"})).setParent(this.mode, DirectionMode.ABOVE);
      this.offerValues(new Value[]{this.baseProtection, this.hideAfter, this.mode, this.nether, this.rotations, this.server, this.sameLine, this.color, this.yaw});
   }

   public void draw(DrawContext context) {
      boolean x = mc.player.getX() > (double)((Integer)this.hideAfter.getValue() * 1000);
      boolean z = mc.player.getZ() > (double)((Integer)this.hideAfter.getValue() * 1000);
      boolean tooFar = x || z;
      boolean hiding = (Boolean)this.baseProtection.getValue() && tooFar;
      boolean showCoords = !hiding;
      if (this.rotations.getParent().isVisible() && (Boolean)this.rotations.getValue()) {
         this.renderRotations(context, showCoords);
      }

      if (showCoords) {
         String directionString = this.getDirectionForDisplay();
         String var10000 = String.valueOf(Formatting.GRAY);
         String coordsString = "XYZ: " + var10000 + this.getRoundedDouble(mc.player.getX()) + String.valueOf(Formatting.BOLD) + ", " + String.valueOf(Formatting.GRAY) + this.getRoundedDouble(mc.player.getY()) + String.valueOf(Formatting.BOLD) + ", " + String.valueOf(Formatting.GRAY) + this.getRoundedDouble(mc.player.getZ());
         if (!mc.world.getRegistryKey().getValue().getPath().equals("the_end") && (Boolean)this.nether.getValue()) {
            coordsString = coordsString + String.valueOf(Formatting.BOLD) + " (" + String.valueOf(Formatting.GRAY) + this.getRoundedDouble(this.getDimensionCoord(mc.player.getX())) + String.valueOf(Formatting.BOLD) + ", " + String.valueOf(Formatting.GRAY) + this.getRoundedDouble(this.getDimensionCoord(mc.player.getZ())) + String.valueOf(Formatting.BOLD) + ")";
         }

         switch((DirectionMode)this.mode.getValue()) {
         case OFF:
         case BOTTOM:
            this.drawText(context, coordsString + (this.mode.getValue() == DirectionMode.BOTTOM ? directionString : ""), 2, context.getScaledWindowHeight() - (this.isChatOpened ? 24 : 10));
            break;
         case ABOVE:
            this.drawText(context, coordsString, 2, context.getScaledWindowHeight() - (this.isChatOpened ? 24 : 10));
            this.drawText(context, this.getFinalDir(), 2, context.getScaledWindowHeight() - (this.isChatOpened ? 24 : 10) - 10);
         }
      }

   }

   private void renderRotations(DrawContext context, boolean showCoords) {
      String var10000 = String.valueOf(Formatting.GRAY);
      String yawText = "Yaw: " + var10000 + String.format("%.2f", MathHelper.wrapDegrees(mc.player.getYaw())) + ((Boolean)this.server.getValue() ? String.valueOf(Formatting.BOLD) + " (" + String.valueOf(Formatting.GRAY) + String.format("%.2f", MathHelper.wrapDegrees(Managers.getRotationManager().getRenderYaw())) + String.valueOf(Formatting.BOLD) + ")" : "");
      var10000 = String.valueOf(Formatting.GRAY);
      String pitchText = "Pitch: " + var10000 + String.format("%.2f", MathHelper.wrapDegrees(mc.player.getPitch())) + ((Boolean)this.server.getValue() ? String.valueOf(Formatting.BOLD) + " (" + String.valueOf(Formatting.GRAY) + String.format("%.2f", MathHelper.wrapDegrees(Managers.getRotationManager().getRenderPitch())) + String.valueOf(Formatting.BOLD) + ")" : "");
      var10000 = String.valueOf(Formatting.GRAY);
      String bodyYaw = "BodyYaw: " + var10000 + String.format("%.2f", MathHelper.wrapDegrees(mc.player.getBodyYaw())) + String.valueOf(Formatting.BOLD) + " (" + String.valueOf(Formatting.GRAY) + "%.2f".formatted(new Object[]{MathHelper.wrapDegrees(Managers.getRotationManager().getRenderBodyYaw())}) + String.valueOf(Formatting.BOLD) + ")";
      int yOffset = context.getScaledWindowHeight() - (showCoords ? (this.isChatOpened ? 34 : 20) : (this.isChatOpened ? 24 : 10));
      if (!(Boolean)this.sameLine.getValue() && !this.isChatOpened) {
         this.drawText(context, yawText, 2, context.getScaledWindowHeight() - (showCoords ? 30 : 20));
         this.drawText(context, pitchText, 2, yOffset);
      } else {
         String bodyYawText = (Boolean)this.server.getValue() ? String.valueOf(Formatting.RESET) + " " + bodyYaw : "";
         this.drawText(context, yawText + bodyYawText + String.valueOf(Formatting.RESET) + " " + pitchText, 2, yOffset);
      }

   }

   private String getFinalDir() {
      String var10000;
      Formatting col;
      if ((Boolean)this.yaw.getValue()) {
         col = (Boolean)this.color.getValue() ? Formatting.RESET : Formatting.GRAY;
         var10000 = String.valueOf(col);
         return var10000 + this.getFixedDirection() + String.valueOf(Formatting.BOLD) + this.getDirectionForDisplay() + ", " + String.valueOf(Formatting.GRAY) + MathUtil.round((double)MathHelper.wrapDegrees(mc.player.getYaw()), 1) + String.valueOf(Formatting.BOLD) + "]";
      } else {
         col = (Boolean)this.color.getValue() ? Formatting.RESET : Formatting.GRAY;
         var10000 = String.valueOf(col);
         return var10000 + this.getFixedDirection() + this.getDirectionForDisplay();
      }
   }

   private String getFixedDirection() {
      switch(mc.player.getHorizontalFacing()) {
      case EAST:
         return "East";
      case WEST:
         return "West";
      case SOUTH:
         return "South";
      case NORTH:
         return "North";
      default:
         return "";
      }
   }

   private static int getDirection4D() {
      return MathHelper.floor((double)(mc.player.getYaw() * 4.0F / 360.0F) + 0.5D) & 3;
   }

   private String getDirectionForDisplay() {
      String var10000;
      switch(getDirection4D()) {
      case 0:
         var10000 = String.valueOf(Formatting.BOLD) + " [" + String.valueOf(Formatting.GRAY) + "+Z" + String.valueOf(Formatting.BOLD) + "]";
         break;
      case 1:
         var10000 = String.valueOf(Formatting.BOLD) + " [" + String.valueOf(Formatting.GRAY) + "-X" + String.valueOf(Formatting.BOLD) + "]";
         break;
      case 2:
         var10000 = String.valueOf(Formatting.BOLD) + " [" + String.valueOf(Formatting.GRAY) + "-Z" + String.valueOf(Formatting.BOLD) + "]";
         break;
      case 3:
         var10000 = String.valueOf(Formatting.BOLD) + " [" + String.valueOf(Formatting.GRAY) + "+X" + String.valueOf(Formatting.BOLD) + "]";
         break;
      default:
         throw new IllegalStateException("Unexpected value: " + getDirection4D());
      }

      return var10000;
   }

   private double getDimensionCoord(double coord) {
      if (mc.world.getRegistryKey().getValue().getPath().equals("the_nether")) {
         return coord * 8.0D;
      } else {
         return !mc.world.getRegistryKey().getValue().getPath().equals("the_nether") ? coord / 8.0D : coord;
      }
   }

   private String getRoundedDouble(double pos) {
      return String.format("%.2f", pos);
   }
}