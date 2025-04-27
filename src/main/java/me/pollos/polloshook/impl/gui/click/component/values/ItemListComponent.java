package me.pollos.polloshook.impl.gui.click.component.values;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ItemListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.item.ToggleableItem;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.impl.gui.click.component.Component; 
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI; 
import me.pollos.polloshook.impl.module.other.colours.Colours; 
import net.minecraft.client.gui.DrawContext;

public class ItemListComponent extends ValueComponent<List<ToggleableItem>, ItemListValue> {
   private final ItemListValue itemListValue;
   private final List<Component> components = new ArrayList();

   public ItemListComponent(ItemListValue itemListValue, Rectangle rect, float offsetX, float offsetY) {
      super(itemListValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), itemListValue);
      this.itemListValue = itemListValue;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
      this.components.forEach((c) -> {
         c.moved(this.getFinishedX(), this.getFinishedY());
      });
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      Rectangle rect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, 12.0F);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, rect);

      if (this.isExtended()) {
         this.components.forEach((c) -> {
            c.render(context, mouseX, mouseY, delta);
         });
         this.setHeight((float)(14 + this.components.size() * 14));
      } else {
         this.setHeight(14.0F);
      }

      Render2DMethods.drawRect(context, this.commonRenderRectangle(), hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());

      Managers.getTextManager().drawString((DrawContext)context, this.isExtended() ? "-" : "+", (double)((int)(this.getFinishedX() + this.getWidth() - 10.0F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1); 

      String label = this.getLabel();
      Managers.getTextManager().drawString((DrawContext)context, label, (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (((List)((ItemListValue)this.getValue()).getValue()).size() == 0) {
         return super.mouseClicked(mouseX, mouseY, button);
      } else {
         Rectangle rect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, 12.0F);
         boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, rect);

         if (hovered && button == 1) {
            this.setExtended(!this.isExtended());
            return true; 
         }

         if (this.isExtended()) {
            for (Component c : components) {
                if (c.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   protected Rectangle commonRenderRectangle() {
      return new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + 14.0F - 0.5F);
   }

   public void setExtended(boolean extended) {
      this.components.clear();
      if (extended) { 
         this.initComponents();
      }
      super.setExtended(extended);
   }

   public void updateVisibility() {
      if (((List)this.getItemListValue().getValue()).size() > 0) {
         this.getItemListValue().setParent(new SupplierParent(() -> {
            return true;
         }, true)); 
      } else {
         this.getItemListValue().setParent(new SupplierParent(() -> {
            return false;
         }, false));
      }
   }

   private void initComponents() {
      float offY = 14.0F; 
      for(Iterator<ToggleableItem> var2 = ((List<ToggleableItem>)this.getValue()).iterator(); var2.hasNext(); offY += 14.0F) {
         ToggleableItem item = var2.next();
         this.components.add(this.createItemChild(item, 0.0F, offY, this.getWidth(), 14.0F));
      }
   }

   private Component createItemChild(ToggleableItem item, float offsetX, float offsetY, float width, float height) {
      return new Component(item.getItemName(), ItemListComponent.this.getFinishedX(), ItemListComponent.this.getFinishedY(), offsetX, offsetY, width, height) {
         public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            Rectangle rect = new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + this.getHeight() - 0.5F);
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, hoveringRect);
            boolean bool = item.isEnabled();

            if (hovered && !bool) {
               Render2DMethods.drawRect(context, rect, 1714631475); 
            }

            if (bool) {
               Render2DMethods.drawRect(context, rect, hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB()); 
            }

            Managers.getTextManager().drawString((DrawContext)context, this.getLabel().replace(" ", ""), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
         }

         public void moved(float parentFinishedX, float parentFinishedY) {
             this.setPosX(parentFinishedX);
             this.setPosY(parentFinishedY);
             this.setFinishedX(this.getPosX() + this.getOffsetX());
             this.setFinishedY(this.getPosY() + this.getOffsetY());
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            if (Render2DMethods.mouseWithinBounds(mouseX, mouseY, hoveringRect) && button == 0) {
               item.toggle(); 
               return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
         }

         @Override
         public Color getColor(int alpha) {
             return ColorUtil.changeAlpha(Colours.get().getColor((Boolean)ClickGUI.get().getHomosexuality().getValue() ? (int)this.getFinishedY() : 0), alpha);
         }

         @Override
         public Color getColor() {
             return this.getColor(125);
         }
      };
   }

   public ItemListValue getItemListValue() {
      return this.itemListValue;
   }

   public List<Component> getComponents() {
      return this.components;
   }
}
