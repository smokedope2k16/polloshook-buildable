package me.pollos.polloshook.impl.gui.click.component.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.list.toggleable.block.BlockListValue;
import me.pollos.polloshook.api.value.value.list.toggleable.block.ToggleableBlock;
import me.pollos.polloshook.api.value.value.parents.SupplierParent;
import me.pollos.polloshook.impl.gui.click.component.Component;
import net.minecraft.client.gui.DrawContext;

public class BlockListComponent extends ValueComponent<List<ToggleableBlock>, BlockListValue> {
   private final BlockListValue blockListValue;
   private final List<Component> components = new ArrayList();

   public BlockListComponent(BlockListValue blockListValue, Rectangle rect, float offsetX, float offsetY) {
      super(blockListValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), blockListValue);
      this.blockListValue = blockListValue;
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
      Managers.getTextManager().drawString((DrawContext)context, this.isExtended() ? "-" : "+", (double)((int)(this.getFinishedX() + 90.0F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
      String label = this.getLabel();
      Managers.getTextManager().drawString((DrawContext)context, label, (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (((List)((BlockListValue)this.getValue()).getValue()).size() == 0) {
         return super.mouseClicked(mouseX, mouseY, button);
      } else {
         Rectangle rect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, 12.0F);
         boolean hovered = Render2DMethods.mouseWithinBounds(mouseX, mouseY, rect);
         if (hovered && button == 1) {
            this.setExtended(!this.isExtended());
         }

         if (this.isExtended()) {
            this.components.forEach((c) -> {
               c.mouseClicked(mouseX, mouseY, button);
            });
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   protected Rectangle commonRenderRectangle() {
      return new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + 14.0F - 0.5F);
   }

   public void setExtended(boolean extended) {
      this.components.clear();
      this.initComponents();
      super.setExtended(extended);
   }

   public void updateVisibility() {
      if (((List)this.getBlockListValue().getValue()).size() > 0) {
         this.getBlockListValue().setParent(new SupplierParent(() -> {
            return true;
         }, false));
      } else {
         this.getBlockListValue().setParent(new SupplierParent(() -> {
            return false;
         }, false));
      }

   }

   private void initComponents() {
      float offY = 14.0F;

      for(Iterator var2 = ((List)((BlockListValue)this.getValue()).getValue()).iterator(); var2.hasNext(); offY += 14.0F) {
         ToggleableBlock block = (ToggleableBlock)var2.next();
         this.components.add(this.createBlockChild(block, this.getFinishedX(), this.getFinishedY(), this.getWidth(), offY));
      }

   }

   private Component createBlockChild(ToggleableBlock block, float finishedX, float finishedY, float width, float offY) {
      return new Component(block.getBlockName(), finishedX, finishedY, 0.0F, offY, width, 14.0F) {
         public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            Rectangle rect = new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + this.getHeight() - 0.5F);
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, hoveringRect);
            boolean bool = block.isEnabled();
            if (hovered && !bool) {
               Render2DMethods.drawRect(context, rect, 1714631475);
            }

            if (bool) {
               Render2DMethods.drawRect(context, rect, hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
            }

            Managers.getTextManager().drawString((DrawContext)context, this.getLabel().replace(" ", ""), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
         }

         public void moved(float posX, float posY) {
            super.moved(posX, posY);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            if (Render2DMethods.mouseWithinBounds(mouseX, mouseY, hoveringRect) && button == 0) {
               block.toggle();
            }

            return super.mouseClicked(mouseX, mouseY, button);
         }
      };
   }

   
   public BlockListValue getBlockListValue() {
      return this.blockListValue;
   }

   
   public List<Component> getComponents() {
      return this.components;
   }
}
