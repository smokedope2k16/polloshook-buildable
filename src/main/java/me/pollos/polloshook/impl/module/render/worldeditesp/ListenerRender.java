package me.pollos.polloshook.impl.module.render.worldeditesp;

import java.awt.Color;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.render.worldeditesp.mode.RenderMode;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class ListenerRender extends ModuleListener<WorldEditESP, RenderEvent> {
   public ListenerRender(WorldEditESP module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (((WorldEditESP)this.module).secondBlock != null || ((WorldEditESP)this.module).firstBlock != null) {
         if (!mc.player.isCreative()) {
            ((WorldEditESP)this.module).secondBlock = null;
            ((WorldEditESP)this.module).firstBlock = null;
         } else {
            MatrixStack matrix = event.getMatrixStack();
            matrix.push();
            RenderMethods.enable3D();
            MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
            Framebuffer framebuffer = mc.getFramebuffer();
            MSAAFramebuffer.start(smoothBuffer, framebuffer);
            Box interpedSecond;
            if (((WorldEditESP)this.module).secondBlock != null && ((WorldEditESP)this.module).firstBlock != null) {
               interpedSecond = Interpolation.interpolatePos(((WorldEditESP)this.module).secondBlock);
               Box second = Interpolation.interpolatePos(((WorldEditESP)this.module).firstBlock);
               Box box = Interpolation.mergeBoxes(interpedSecond, second);
               this.render(matrix, box, ((WorldEditESP)this.module).fillColor.getColor(), ((WorldEditESP)this.module).lineColor.getColor());
            }

            Color firstColor;
            Color secondColor;
            if (((WorldEditESP)this.module).firstBlock != null) {
               interpedSecond = Interpolation.interpolatePos(((WorldEditESP)this.module).firstBlock);
               firstColor = ColorUtil.changeAlpha(((WorldEditESP)this.module).firstColor.getColor(), ((WorldEditESP)this.module).fillColor.getColor().getAlpha());
               secondColor = ColorUtil.changeAlpha(((WorldEditESP)this.module).firstColor.getColor(), ((WorldEditESP)this.module).lineColor.getColor().getAlpha());
               this.render(matrix, interpedSecond, firstColor, secondColor);
            }

            if (((WorldEditESP)this.module).secondBlock != null) {
               interpedSecond = Interpolation.interpolatePos(((WorldEditESP)this.module).secondBlock);
               firstColor = ColorUtil.changeAlpha(((WorldEditESP)this.module).secondColor.getColor(), ((WorldEditESP)this.module).fillColor.getColor().getAlpha());
               secondColor = ColorUtil.changeAlpha(((WorldEditESP)this.module).secondColor.getColor(), ((WorldEditESP)this.module).lineColor.getColor().getAlpha());
               this.render(matrix, interpedSecond, firstColor, secondColor);
            }

            MSAAFramebuffer.end(smoothBuffer, framebuffer);
            RenderMethods.disable3D();
            matrix.pop();
         }
      }
   }

   protected void render(MatrixStack matrix, Box box, Color color, Color secondColor) {
      if ((Boolean)((WorldEditESP)this.module).cross.getValue()) {
         RenderMethods.drawCrossBox(matrix, box, color, 1.3F, false);
      }

      switch((RenderMode)((WorldEditESP)this.module).mode.getValue()) {
      case BOTH:
         RenderMethods.drawBox(matrix, box, color);
         RenderMethods.drawOutlineBox(matrix, box, secondColor, 1.3F);
         break;
      case FILL:
         RenderMethods.drawBox(matrix, box, color);
         break;
      case OUTLINE:
         RenderMethods.drawOutlineBox(matrix, box, secondColor, 1.3F);
      }

   }
}
