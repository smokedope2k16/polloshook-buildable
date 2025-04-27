package me.pollos.polloshook.impl.gui.click.component.values;


import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.util.obj.rectangle.Rectangle;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.gui.click.component.Component;
import me.pollos.polloshook.impl.module.other.clickgui.ClickGUI;
import net.minecraft.client.gui.DrawContext;

public class ValueComponent<V, T extends Value<V>> extends Component {
   protected final T value;

   public ValueComponent(String label, float posX, float posY, float offsetX, float offsetY, float width, float height, T setting) {
      super(label, posX, posY, offsetX, offsetY, width, height);
      this.value = setting;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      Rectangle indicatorRectangle;
      if ((Boolean)ClickGUI.get().getFutureBox().getValue()) {
         indicatorRectangle = this.commonRenderRectangle().copy().setX(this.getFinishedX() + 3.0F);
         Render2DMethods.drawGradientRect(context, indicatorRectangle, false, 553648127, 285212671);
      }

      indicatorRectangle = new Rectangle(this.getFinishedX() + 1.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + 2.5F, this.getFinishedY() + this.getHeight() - 0.5F);
      Render2DMethods.drawRect(context, indicatorRectangle, this.getColor(200).getRGB());
   }

   public boolean hoveringCommonRectangle(int mouseX, int mouseY) {
      Rectangle commonRect = new Rectangle(this.getFinishedX() + 1.0F, this.getFinishedY() + 1.0F, this.getWidth() - 2.0F, this.getHeight() - 2.0F);
      return Render2DMethods.mouseWithinBounds((double)mouseX, (double)mouseY, commonRect);
   }

   protected Rectangle commonRenderRectangle() {
      return new Rectangle(this.getFinishedX() + 2.5F, this.getFinishedY() + 1.0F, this.getFinishedX() + this.getWidth() - 1.5F, this.getFinishedY() + this.getHeight() - 0.5F);
   }

   
   public T getValue() {
      return this.value;
   }
}
