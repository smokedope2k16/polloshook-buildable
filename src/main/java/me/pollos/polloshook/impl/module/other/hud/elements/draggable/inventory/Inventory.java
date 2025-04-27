package me.pollos.polloshook.impl.module.other.hud.elements.draggable.inventory;

import java.awt.Color;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class Inventory extends DraggableHUDModule {
   private final Value<Boolean> rect = new Value(true, new String[]{"Rect", "rectangle"});
   private final Value<Boolean> outline;
   private final ColorValue color;

   public Inventory() {
      super(new String[]{"Inventory", "inv"});
      this.outline = (new Value(true, new String[]{"Outline", "Outlined", "line"})).setParent(this.rect);
      this.color = (new ColorValue(new Color(165, 113, 255), true, new String[]{"Color", "colour", "olor", "c"})).setParent(this.outline);
      this.offerValues(new Value[]{this.rect, this.outline, this.color});
   }

   public void setDefaultPosition(DrawContext context) {
      int x = context.getScaledWindowWidth() - 382;
      int y = context.getScaledWindowHeight() - 52;
      this.setTextX((float)x);
      this.setTextY((float)y);
      this.setTextWidth(144.0F);
      this.setTextHeight(50.0F);
   }

   public void draw(DrawContext context) {
      if ((Boolean)this.rect.getValue()) {
         Render2DMethods.enable2D();
         if ((Boolean)this.outline.getValue()) {
            Render2DMethods.drawBorderedRect(context, this.getTextX(), this.getTextY(), this.getTextX() + this.getTextWidth(), this.getTextY() + this.getTextHeight(), 0.5F, 1023410176, this.color.getColor().getRGB());
         } else {
            Render2DMethods.drawRect(context, this.getTextX(), this.getTextY(), this.getTextX() + this.getTextWidth(), this.getTextY() + this.getTextHeight(), 1023410176);
         }

         Render2DMethods.disable2D();
      }

      context.getMatrices().push();

      for(int i = 0; i < 27; ++i) {
         ItemStack stack = (ItemStack)mc.player.getInventory().main.get(i + 9);
         int itemPosX = (int)(this.getTextX() + (float)(i % 9 * 16));
         int itemPosY = (int)(this.getTextY() + (float)(i / 9 * 16));
         context.drawItem(stack, itemPosX, itemPosY);
         context.drawItemInSlot(mc.textRenderer, stack, itemPosX, itemPosY);
      }

      context.getMatrices().pop();
   }
}
