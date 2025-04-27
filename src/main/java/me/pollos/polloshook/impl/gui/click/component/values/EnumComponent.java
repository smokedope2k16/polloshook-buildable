package me.pollos.polloshook.impl.gui.click.component.values;

import java.util.ArrayList;
import java.util.Objects;

import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.constant.EnumValue;
import me.pollos.polloshook.impl.gui.click.component.Component;
import me.pollos.polloshook.impl.module.misc.chatappend.mode.ChatAppendMode;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import me.pollos.polloshook.impl.module.render.nametags.mode.UserSymbolMode;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class EnumComponent<E extends Enum<E>> extends ValueComponent<E, EnumValue<E>> {
   private final EnumValue<E> enumValue;
   private final ArrayList<Component> enumComponents = new ArrayList();
   private final float goHomeLilNigga2 = 14.0F;

   public EnumComponent(EnumValue<E> enumValue, Rectangle rect, float offsetX, float offsetY) {
      super(enumValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), enumValue);
      this.enumValue = enumValue;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
      this.enumComponents.forEach((c) -> {
         c.moved(this.getFinishedX(), this.getFinishedY());
      });
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      Rectangle rect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, 12.0F);
      boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, rect);
      if (this.isExtended()) {
         if (!(Boolean)ClickGUI.get().getEnumDropdown().getValue()) {
            this.setExtended(false);
         }

         this.enumComponents.forEach((c) -> {
            c.render(context, mouseX, mouseY, delta);
         });
         this.setHeight((float)(14 + this.enumComponents.size() * 14));
      } else {
         this.setHeight(14.0F);
      }

      Render2DMethods.drawRect(context, this.commonRenderRectangle(), hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
      String var10000 = this.getLabel();
      String label = var10000 + ": " + this.getEnumValue().getStylizedName();
      Managers.getTextManager().drawString((DrawContext)context, label, (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + 7.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = this.hoveringCommonRectangle((int)mouseX, (int)mouseY);
      if (hovered) {
         if ((Boolean)ClickGUI.get().getEnumDropdown().getValue()) {
            this.enumComponents.forEach((c) -> {
               c.mouseClicked(mouseX, mouseY, button);
            });
            if (this.getEnumValue().getParent().isVisible() && (button == 1 || button == 0)) {
               this.setExtended(!this.isExtended());
            }
         } else if (button == 0) {
            this.click();
            this.getEnumValue().increment();
         } else if (button == 1) {
            this.click();
            this.getEnumValue().decrement();
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return super.mouseReleased(mouseX, mouseY, button);
   }

   protected Rectangle commonRenderRectangle() {
      return new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + 14.0F - 0.5F);
   }

   public void setExtended(boolean extended) {
      this.enumComponents.clear();
      this.initEnums();
      super.setExtended(extended);
   }

   private void initEnums() {
      float offY = 14.0F;
      Enum<?>[] var2 = (Enum<?>[]) this.getEnumValue().getValue().getDeclaringClass().getEnumConstants();
      int var3 = var2.length;

      for (int var4 = 0; var4 < var3; ++var4) {
         E e = (E) var2[var4];
         this.enumComponents.add(this.createChild(e, this.getFinishedX(), this.getFinishedY(), this.getWidth(), offY));
         offY += 14.0F;
      }
   }


   private Component createChild(E e, float finishedX, float finishedY, float width, float offY) {
      return new Component(this.getLabelForComponent(e), finishedX, finishedY, 0.0F, offY, width, 14.0F) {
         public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
            Rectangle rect = new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + this.getHeight() - 0.5F);
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            boolean hovered = Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, hoveringRect);
            Render2DMethods.drawRect(context, rect, hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
            Managers.getTextManager().drawString((DrawContext)context, this.getLabel(), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
         }

         public void moved(float posX, float posY) {
            super.moved(posX, posY);
         }

         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            Rectangle hoveringRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
            if (Render2DMethods.mouseWithinBounds(mouseX, mouseY, hoveringRect)) {
               ((EnumValue)EnumComponent.this.value).setValue(e);
            }

            return super.mouseClicked(mouseX, mouseY, button);
         }
      };
   }

   private String getLabelForComponent(E e) {
      String var10000;
      if (e instanceof UserSymbolMode) {
         UserSymbolMode usm = (UserSymbolMode)e;
         var10000 = ((EnumValue)this.value).getStylizedName(e);
         return var10000 + " (" + usm.getSymbol() + ")";
      } else if (e instanceof Formatting) {
         Formatting formatting = (Formatting)e;
         var10000 = ((EnumValue)this.value).getStylizedName(e);
         return var10000 + String.valueOf(formatting) + " (\ud83d\udc14)";
      } else {
         if (e instanceof ChatAppendMode) {
            ChatAppendMode mode = (ChatAppendMode)e;
            if (mode != ChatAppendMode.OFF) {
               var10000 = mode.getString().substring(mode == ChatAppendMode.CHACHOOXWARE ? 1 : 3);
               return var10000;
            }
         }

         var10000 = ((EnumValue)this.value).getStylizedName(e);
         return var10000;
      }
   }

   
   public EnumValue<E> getEnumValue() {
      return this.enumValue;
   }

   
   public ArrayList<Component> getEnumComponents() {
      return this.enumComponents;
   }

   
   public float getGoHomeLilNigga2() {
      Objects.requireNonNull(this);
      return 14.0F;
   }
}
