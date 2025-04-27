package me.pollos.polloshook.impl.gui.click.component.values;


import me.pollos.polloshook.api.managers.Managers;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.gui.DrawContext;

public class BooleanComponent extends ValueComponent<Boolean, Value<Boolean>> {
   private final Value<Boolean> booleanValue;

   public BooleanComponent(Value<Boolean> booleanValue, Rectangle rect, float offsetX, float offsetY) {
      super(booleanValue.getLabel(), rect.getX(), rect.getY(), offsetX, offsetY, rect.getWidth(), rect.getHeight(), booleanValue);
      this.booleanValue = booleanValue;
   }

   public void moved(float posX, float posY) {
      super.moved(posX, posY);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      boolean hovered = this.hoveringCommonRectangle(mouseX, mouseY);
      if (hovered && !(Boolean)this.getBooleanValue().getValue()) {
         Render2DMethods.drawRect(context, this.commonRenderRectangle(), 1714631475);
      }

      if ((Boolean)this.getBooleanValue().getValue()) {
         Render2DMethods.drawRect(context, this.commonRenderRectangle(), hovered ? this.getColor().darker().getRGB() : this.getColor().getRGB());
      }

      Managers.getTextManager().drawString((DrawContext)context, this.getLabel(), (double)((int)(this.getFinishedX() + 4.5F)), (double)((int)(this.getFinishedY() + this.getHeight() / 2.0F - (float)(Managers.getTextManager().getHeight() >> 1))), -1);
   }

   public void keyPressed(int keyCode, int scanCode, int modifiers) {
      super.keyPressed(keyCode, scanCode, modifiers);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      boolean hovered = this.hoveringCommonRectangle((int)mouseX, (int)mouseY);
      if (hovered && button == 0) {
         this.click();
         this.getBooleanValue().setValue(!(Boolean)this.getBooleanValue().getValue());
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
      return super.mouseReleased(mouseX, mouseY, mouseButton);
   }

   
   public Value<Boolean> getBooleanValue() {
      return this.booleanValue;
   }
}
