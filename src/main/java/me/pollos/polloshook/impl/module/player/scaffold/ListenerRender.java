package me.pollos.polloshook.impl.module.player.scaffold;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.minecraft.render.RenderPosition;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.manager.Manager;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class ListenerRender extends ModuleListener<Scaffold, RenderEvent> {
   public ListenerRender(Scaffold module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if ((Boolean)((Scaffold)this.module).render.getValue()) {
         MatrixStack stack = event.getMatrixStack();
         stack.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         List<RenderPosition> removedBlocks = new ArrayList();
         Iterator var6 = (new ArrayList(((Scaffold)this.module).positionList)).iterator();

         while(var6.hasNext()) {
            RenderPosition renderPos = (RenderPosition)var6.next();
            if (mc.world.getBlockState(renderPos.getPos()).isAir()) {
               removedBlocks.add(renderPos);
            } else {
               BlockPos pos = renderPos.getPos();
               int alpha = (int)ColorUtil.fade((double)renderPos.getTime(), (double)Manager.get().getFadeTime());
               if (alpha == 0) {
                  removedBlocks.add(renderPos);
               } else {
                  Box box = Interpolation.interpolatePos(pos);
                  RenderMethods.drawBox(stack, box, ColorUtil.changeAlpha(Manager.get().getBlocksColor(), alpha / 4));
                  RenderMethods.drawOutlineBox(stack, box, ColorUtil.changeAlpha(Manager.get().getBlocksColor(), alpha), 1.3F);
               }
            }
         }

         this.removeRenderPositions(removedBlocks);
         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         stack.pop();
      }
   }

   private void removeRenderPositions(List<RenderPosition> positions) {
      if (!positions.isEmpty()) {
         Iterator var2 = (new ArrayList(positions)).iterator();

         while(var2.hasNext()) {
            RenderPosition renderPos = (RenderPosition)var2.next();
            positions.remove(renderPos);
         }

      }
   }
}
