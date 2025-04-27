package me.pollos.polloshook.impl.module.player.fastbreak;

import java.awt.Color;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.impl.events.render.Render2DEvent;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class Listener2DRender extends ModuleListener<FastBreak, Render2DEvent> {
   public Listener2DRender(FastBreak module) {
      super(module, Render2DEvent.class);
   }

   public void call(Render2DEvent event) {
      if ((Boolean)((FastBreak)this.module).drawCircle.getValue()) {
         Window window = mc.getWindow();
         float x = (float)window.getScaledWidth() / 2.0F;
         float y = (float)window.getScaledHeight() / 2.0F;
         MatrixStack matrix = event.getContext().getMatrices();
         matrix.push();
         Render2DMethods.enable2D();
         float yOffset = mc.getWindow().isFullscreen() ? 0.0F : 0.5F;
         Render2DMethods.drawCircle(event.getContext(), x - 0.5F, y - yOffset, 8.0F, ((FastBreak)this.module).getMaxDamage(), ((FastBreak)this.module).getProgressColor().getRGB());
         Render2DMethods.drawCircle(event.getContext(), x - 0.5F, y - yOffset, 8.5F, ((FastBreak)this.module).getMaxDamage(), Color.BLACK.getRGB());
         Render2DMethods.disable2D();
         matrix.pop();
      }

   }
}
