package me.pollos.polloshook.impl.module.other.hud.elements.draggable.dotgod;

import java.awt.Color;
import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.minecraft.network.NetworkUtil;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;

public class DotGod extends DraggableHUDModule {
   private final Value<Boolean> watermark = new Value(false, new String[]{"Watermark", "mark"});
   private final ColorValue trueColor = new ColorValue(new Color(75, 255, 0), false, new String[]{"TrueColor", "yescolor"});
   private final ColorValue falseColor;
   private final Value<Boolean> colorPing;
   private final Value<Boolean> colorTrue;
   protected boolean htrCheck;
   protected boolean plrCheck;
   protected boolean lbyCheck;
   private int highest;

   public DotGod() {
      super(new String[]{"DotGod", "god"});
      this.falseColor = new ColorValue(Color.RED, false, new String[]{"FalseColor", "nocolor"});
      this.colorPing = new Value(false, new String[]{"ColorPing", "ping"});
      this.colorTrue = (new Value(false, new String[]{"AlwaysTrue", "alwaysfacts"})).setParent(this.colorPing, true);
      this.highest = 0;
      this.offerValues(new Value[]{this.watermark, this.trueColor, this.falseColor, this.colorPing, this.colorTrue});
      this.offerListeners(new Listener[]{new ListenerUpdate(this)});
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX(2.0F);
      this.setTextHeight(200.0F);
      this.setTextWidth(25.0F);
      this.setTextHeight((Boolean)this.watermark.getValue() ? 60.0F : 50.0F);
   }

   public void draw(DrawContext context) {
      int offsetY = 0;
      int x = (int)this.getTextX();
      String htrStr;
      if ((Boolean)this.watermark.getValue()) {
         htrStr = "Easy decompile - IEatHex__";
         this.drawText(context, htrStr, x, (int)(this.getTextY() + (float)offsetY), Colours.get().getColorRGB());
         offsetY += 10;
      }

      htrStr = "HTR";
      this.renderText(context, htrStr, x, (int)(this.getTextY() + (float)offsetY), PvPComponent.HTR);
      offsetY += 10;
      String plrStr = "PLR";
      this.renderText(context, plrStr, x, (int)(this.getTextY() + (float)offsetY), PvPComponent.PLR);
      offsetY += 10;
      int totems = InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING);
      String totemStr = String.valueOf(totems);
      this.renderText(context, totemStr, x, (int)(this.getTextY() + (float)offsetY), PvPComponent.TOTEMS);
      offsetY += 10;
      int var10000 = NetworkUtil.getPing();
      String pingStr = "PING " + var10000;
      this.renderText(context, pingStr, x, (int)(this.getTextY() + (float)offsetY), PvPComponent.PING);
      offsetY += 10;
      String lbyStr = "LBY";
      this.renderText(context, lbyStr, x, (int)(this.getTextY() + (float)offsetY), PvPComponent.LBY);
      this.setTextWidth((float)this.highest);
      this.setTextHeight((Boolean)this.watermark.getValue() ? 60.0F : 50.0F);
   }

   protected void renderText(DrawContext context, String text, int x, int y, PvPComponent component) {
      this.drawText(context, text, x, y, this.getColorFromComponent(component), true);
      this.highest = (int)Math.max((float)this.highest, this.getWidth(text));
   }

   protected int getColorFromComponent(PvPComponent comp) {
      int yesColor = this.trueColor.getColor().getRGB();
      int noColor = this.falseColor.getColor().getRGB();
      switch(comp) {
      case TOTEMS:
         if (InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING) > 0) {
            return yesColor;
         }

         return noColor;
      case HTR:
         if (this.htrCheck) {
            return yesColor;
         }

         return noColor;
      case PLR:
         if (this.plrCheck) {
            return yesColor;
         }

         return noColor;
      case PING:
         if (!(Boolean)this.colorPing.getValue()) {
            return (Boolean)this.colorTrue.getValue() ? yesColor : noColor;
         } else {
            if (NetworkUtil.getPing() <= 100) {
               return yesColor;
            }

            return noColor;
         }
      case LBY:
         if (this.lbyCheck) {
            return yesColor;
         }

         return noColor;
      default:
         return noColor;
      }
   }
}