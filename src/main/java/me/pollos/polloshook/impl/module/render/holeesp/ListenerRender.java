package me.pollos.polloshook.impl.module.render.holeesp;

import java.awt.Color;
import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.api.util.obj.hole.Hole;
import me.pollos.polloshook.api.util.obj.hole.Hole2x1;
import me.pollos.polloshook.api.util.obj.hole.SafetyEnum;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.render.holeesp.mode.OutlineMode;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class ListenerRender extends ModuleListener<HoleESP, RenderEvent> {
   public ListenerRender(HoleESP module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      MatrixStack matrix = event.getMatrixStack();
      matrix.push();
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      Iterator var5;
      if (!((HoleESP)this.module).holes.isEmpty()) {
         var5 = ((HoleESP)this.module).holes.iterator();

         label85:
         while(true) {
            Color color;
            Box bb;
            Hole2x1 hole2x1;
            do {
               Hole hole;
               do {
                  do {
                     if (!var5.hasNext()) {
                        break label85;
                     }

                     hole = (Hole)var5.next();
                     if (hole == null) {
                        return;
                     }
                  } while(hole.getSafety() == null);

                  Color var10000;
                  switch(hole.getSafety()) {
                  case OBBY:
                     var10000 = ((HoleESP)this.module).getObbyColor();
                     break;
                  case MIXED:
                     var10000 = ((HoleESP)this.module).getMixedColor();
                     break;
                  case BEDROCK:
                     var10000 = ((HoleESP)this.module).getBedrockColor();
                     break;
                  case TERRAIN:
                     var10000 = ((HoleESP)this.module).getTerrainColor();
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
                  }

                  color = var10000;
                  if ((Boolean)((HoleESP)this.module).custom2x1Color.getValue() && hole instanceof Hole2x1) {
                     color = (Color)((HoleESP)this.module).doublesColor.getValue();
                  }

                  color = ColorUtil.changeAlpha(color, (Integer)((HoleESP)this.module).boxAlpha.getValue());
               } while(hole.getSafety() == SafetyEnum.TERRAIN && !(Boolean)((HoleESP)this.module).terrain.getValue());

               float height = hole instanceof Hole2x1 ? (Float)((HoleESP)this.module).twoByOneHeight.getValue() : (Float)((HoleESP)this.module).height.getValue();
               bb = Interpolation.interpolatePos(hole.getPos(), height);
               if (hole instanceof Hole2x1) {
                  hole2x1 = (Hole2x1)hole;
                  bb = new Box((double)hole2x1.getPos().getX() - Interpolation.getCameraPos().x, (double)hole2x1.getPos().getY() - Interpolation.getCameraPos().y, (double)hole2x1.getPos().getZ() - Interpolation.getCameraPos().z, (double)(hole2x1.getSecondPos().getX() + 1) - Interpolation.getCameraPos().x, (double)((float)hole2x1.getSecondPos().getY() + height) - Interpolation.getCameraPos().y, (double)(hole2x1.getSecondPos().getZ() + 1) - Interpolation.getCameraPos().z);
               }

               if (!(hole instanceof Hole2x1)) {
                  break;
               }

               hole2x1 = (Hole2x1)hole;
            } while(hole2x1.isProtocolSafe() && !(Boolean)((HoleESP)this.module).protocolSafe.getValue());

            if (Interpolation.isVisible(bb, event)) {
               RenderMethods.drawBox(matrix, bb, color);
               this.drawOutline(matrix, bb, ColorUtil.changeAlpha(color, (Integer)((HoleESP)this.module).outlineAlpha.getValue()));
            }
         }
      }

      if ((Boolean)((HoleESP)this.module).voidHole.getValue() && !((HoleESP)this.module).voidHoles.isEmpty()) {
         var5 = ((HoleESP)this.module).voidHoles.iterator();

         while(var5.hasNext()) {
            BlockPos pos = (BlockPos)var5.next();
            Box bb = Interpolation.interpolatePos(pos, 0.0F);
            if (Interpolation.isVisible(bb, event)) {
               Color color = ColorUtil.changeAlpha(((HoleESP)this.module).voidColor.getColor(), (Integer)((HoleESP)this.module).boxAlpha.getValue());
               RenderMethods.drawBox(matrix, bb, color);
               this.drawOutline(matrix, bb, ColorUtil.changeAlpha(color, (Integer)((HoleESP)this.module).outlineAlpha.getValue()));
            }
         }
      }

      MSAAFramebuffer.end(smoothBuffer, framebuffer);
      RenderMethods.disable3D();
      matrix.pop();
   }

   private void drawOutline(MatrixStack stack, Box bb, Color color) {
      switch((OutlineMode)((HoleESP)this.module).outlineMode.getValue()) {
      case NORMAL:
         RenderMethods.drawOutlineBox(stack, bb, ColorUtil.changeAlpha(color, color.getAlpha()), (Float)((HoleESP)this.module).lineWidth.getValue());
         break;
      case CROSS:
         boolean isBox = (double)(Float)((HoleESP)this.module).height.getValue() != 0.0D;
         RenderMethods.drawCrossBox(stack, bb, ColorUtil.changeAlpha(color, color.getAlpha()), (Float)((HoleESP)this.module).lineWidth.getValue(), isBox);
         if (isBox) {
            RenderMethods.drawOutlineBox(stack, bb, ColorUtil.changeAlpha(color, color.getAlpha()), (Float)((HoleESP)this.module).lineWidth.getValue());
         }
      }

   }
}
