package me.pollos.polloshook.impl.module.other.hud.elements.draggable.velocitygraph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.pollos.polloshook.api.minecraft.render.Render2DMethods;
import me.pollos.polloshook.api.module.hud.DraggableHUDModule;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.value.value.ColorValue;
import me.pollos.polloshook.api.value.value.NumberValue;
import me.pollos.polloshook.api.value.value.Value;
import net.minecraft.client.gui.DrawContext;

public class VelocityGraph extends DraggableHUDModule {
   private final NumberValue<Float> yOffset = new NumberValue(6.4F, 0.1F, 10.0F, 0.1F, new String[]{"YOffset", "yoff"});
   private final NumberValue<Float> divide = new NumberValue(3.0F, 1.0F, 5.0F, 0.1F, new String[]{"DivideFactor", "divide"});
   private final NumberValue<Integer> speed = new NumberValue(1, 1, 5, new String[]{"SpeedFactor", "sped", "speed"});
   private final Value<Boolean> fade = new Value(false, new String[]{"Fade", "fadeout"});
   private final ColorValue color = new ColorValue(new Color(-1073741825, false), true, new String[]{"Color", "col"});
   private int frame = 0;
   private final List<Double> linePoints = new ArrayList(100);

   public VelocityGraph() {
      super(new String[]{"VelocityGraph", "motionchart"});
      this.offerValues(new Value[]{this.yOffset, this.divide, this.speed, this.fade, this.color});
   }

   public void setDefaultPosition(DrawContext context) {
      this.setTextX((float)context.getScaledWindowWidth() / 2.0F);
      this.setTextY((float)context.getScaledWindowHeight() - 80.0F);
      this.setTextWidth(100.0F);
      this.setTextHeight(5.0F);
   }

   public void draw(DrawContext context) {
      if (mc.player != null) {
         this.tickSpeed();
         int w = (int)this.getTextX();
         int textHeight = (int)this.getTextHeight();

         for(int i = 0; i < this.linePoints.size() - 1; ++i) {
            double y1 = (Double)this.linePoints.get(i) + (double)this.getTextY() + (double)textHeight;
            double y2 = (Double)this.linePoints.get(i + 1) + (double)this.getTextY() + (double)textHeight;
            int x1 = w + i;
            int x2 = w + i + 1;
            double factor = Math.abs((double)i - (double)this.linePoints.size() / 2.0D) / ((double)this.linePoints.size() / 2.0D);
            float fadeAlpha = (Boolean)this.fade.getValue() ? (float)(1.0D - factor) : 1.0F;
            float alpha = fadeAlpha * (float)this.color.getColor().getAlpha();
            Render2DMethods.renderLine(context.getMatrices(), ColorUtil.changeAlpha(this.color.getColor(), ColorUtil.fixColor((int)alpha)), (double)x1, (double)((int)y1), (double)x2, (double)((int)y2));
         }
      }

   }

   private void tickSpeed() {
      double x = mc.player.getVelocity().x;
      double y = mc.player.getVelocity().y;
      double z = mc.player.getVelocity().z;
      double totalSpeed = Math.sqrt(x * x + y * y + z * z);
      double scaledSpeed = totalSpeed * 100.0D;
      double clampedY = -Math.min(scaledSpeed / (double)(Float)this.divide.getValue(), scaledSpeed * (double)(Float)this.divide.getValue());
      if (this.frame % (Integer)this.speed.getValue() == 0) {
         this.linePoints.add(clampedY);
         if (this.linePoints.size() > 100) {
            this.linePoints.removeFirst();
         }
      }

      ++this.frame;
   }
}
