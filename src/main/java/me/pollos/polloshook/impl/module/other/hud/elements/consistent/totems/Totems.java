package me.pollos.polloshook.impl.module.other.hud.elements.consistent.totems;

import java.awt.Color;
import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.entity.PlayerUtil;
import me.pollos.polloshook.api.minecraft.inventory.InventoryUtil;
import me.pollos.polloshook.api.module.hud.HUDModule;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.module.other.hud.elements.consistent.armor.Armor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

public class Totems extends HUDModule {
   private final Value<Boolean> global = new Value(true, new String[]{"GlobalColor", "global", "globalcolor", "synced", "sync"});
   private final NumberValue<Integer> xOffset = new NumberValue(0, -15, 15, new String[]{"OffsetX", "x"});
   private final NumberValue<Integer> yOffset = new NumberValue(0, -15, 15, new String[]{"OffsetY", "y"});
   private final ItemStack TOTEM_STACK;

   public Totems() {
      super(new String[]{"Totems", "totemcount"});
      this.TOTEM_STACK = new ItemStack(Items.TOTEM_OF_UNDYING);
      this.offerValues(new Value[]{this.global, this.xOffset, this.yOffset});
   }

   public void draw(DrawContext context) {
      if (!PlayerUtil.isSpectator()) {
         this.drawTotem(context);
      }

   }

   public void drawTotem(DrawContext context) {
      int totems = InventoryUtil.getItemCount(Items.TOTEM_OF_UNDYING);
      if (totems > 0) {
         int width = context.getScaledWindowWidth();
         int height = context.getScaledWindowHeight();
         int i = width / 2;

         int y = height - ((Armor)Managers.getModuleManager().getHUD(Armor.class)).getArmorY();
         context.drawItem(this.TOTEM_STACK, i - 6 + (Integer)this.xOffset.getValue(), y - 2 + (Integer)this.yOffset.getValue());

         context.getMatrices().push();
         context.getMatrices().translate(0.0F, 0.0F, 200.0F);

         String totemCountString = String.valueOf(totems); 

         int renderY = y + 9 - 2 + (Integer)this.yOffset.getValue();
         int renderX = (int)((float)(i + 19 - 2) - this.getWidth(totemCountString) - 7.0F + (float)(Integer)this.xOffset.getValue());
         Color color = (Boolean)this.global.getValue() ? this.getColor(renderY) : new Color(this.colorFromCount());
         this.drawText(context, totemCountString, renderX, renderY, color.getRGB(), !(Boolean)this.global.getValue());

         context.getMatrices().pop();
      }
   }


   public int colorFromCount() {
      int count = InventoryUtil.getItemCount(this.TOTEM_STACK.getItem());
      if (count >= 6) {
         return Formatting.GREEN.getColorValue();
      } else if (count == 5) {
         return Formatting.DARK_GREEN.getColorValue();
      } else if (count == 4) {
         return Formatting.WHITE.getColorValue();
      } else if (count == 3) {
         return Formatting.GOLD.getColorValue();
      } else if (count == 2) {
         return Formatting.RED.getColorValue();
      } else {
         return count == 1 ? Formatting.DARK_RED.getColorValue() : Formatting.GREEN.getColorValue();
      }
   }
}