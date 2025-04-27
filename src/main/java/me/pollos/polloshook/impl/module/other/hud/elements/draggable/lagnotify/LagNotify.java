package me.pollos.polloshook.impl.module.other.hud.elements.draggable.lagnotify;

import me.pollos.polloshook.api.event.bus.Listener;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.math.StopWatch;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import me.pollos.polloshook.impl.gui.editor.core.PollosHUD;
import net.minecraft.client.gui.DrawContext;

public class LagNotify extends DraggableHUDModule {
   private final NumberValue<Float> timeout = (new NumberValue(1.0F, 1.0F, 5.0F, 0.1F, new String[]{"Timeout", "time"})).withTag("second");
   protected final StopWatch timer = new StopWatch();

   public LagNotify() {
      super(new String[]{"LagNotify", "response"});
      this.offerValues(new Value[]{this.timeout});
      this.offerListeners(new Listener[]{new ListenerReceive(this)});
   }

   protected void onToggle() {
      this.timer.reset();
   }

   public void draw(DrawContext context) {
      if ((float)this.timer.getTime() >= (Float)this.timeout.getValue() * 1000.0F || mc.currentScreen instanceof PollosHUD) {
         this.drawText(context, this.getString(), (int)this.getTextX(), (int)this.getTextY());
      }

      this.setTextHeight(10.0F);
      this.setTextWidth(this.getWidth(this.getString()));
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX((float)context.getScaledWindowWidth() / 2.0F - this.getWidth(this.getString()) / 2.0F + 2.0F);
      this.setTextY(12.0F);
      this.setTextHeight(10.0F);
      this.setTextWidth(this.getWidth(this.getString()));
   }

   private String getString() {
      Object[] var10001 = new Object[]{(float)this.timer.getTime() / 1000.0F};
      return "Server hasn't responded in " + String.format("%.2f", var10001) + "s";
   }
}
