package me.pollos.polloshook.impl.module.misc.autoreconnect.util;

import me.pollos.polloshook.api.value.value.NumberValue;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class AutoReconnectSliderWidget extends SliderWidget {
   private final NumberValue<Float> numberValue;

   public AutoReconnectSliderWidget(NumberValue<Float> value) {
      super(0, 0, 200, 20, Text.literal("Reconnect Delay: %.1fs".formatted(new Object[]{value.getValue()})), (double)((Float)value.getValue() / 100.0F));
      this.numberValue = value;
   }

   protected void updateMessage() {
      this.setMessage(Text.literal("Reconnect Delay: %.1fs".formatted(new Object[]{this.numberValue.getValue()})));
   }

   protected void applyValue() {
      if (this.active) {
         this.numberValue.setValue(((float)this.value * 100.0F));
      }

   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return false;
   }

   public void onClick(double mouseX, double mouseY) {
      if (this.active) {
         super.onClick(mouseX, mouseY);
      }
   }

   protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
      if (this.active) {
         super.onDrag(mouseX, mouseY, deltaX, deltaY);
      }
   }
}
