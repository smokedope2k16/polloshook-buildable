package me.pollos.polloshook.impl.module.player.blink;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.player.blink.mode.BlinkMode;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class ListenerRender3D extends ModuleListener<Blink, RenderEvent> {
   public ListenerRender3D(Blink module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if ((Boolean)((Blink)this.module).breadCrumbs.getValue() && ((Blink)this.module).mode.getValue() != BlinkMode.FAKE_LAG) {
         if (MovementUtil.isUpdatingPos()) {
            ((Blink)this.module).positions.add(mc.player.getPos());
         }

         MatrixStack matrix = event.getMatrixStack();
         matrix.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         Tessellator tessellator = Tessellator.getInstance();
         RenderSystem.lineWidth(1.3F);
         RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
         RenderSystem.defaultBlendFunc();
         BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
         Vec3d prevVec = null;
         int i = 0;

         Vec3d vec;
         for(Iterator var9 = (new ArrayList(((Blink)this.module).positions)).iterator(); var9.hasNext(); prevVec = vec) {
            vec = (Vec3d)var9.next();
            if (prevVec != null) {
               RenderMethods.drawLine(matrix, bufferBuilder, Interpolation.interpolateVec(vec), Interpolation.interpolateVec(prevVec), Colours.get().getColourCustomAlpha(175));
               ++i;
            }
         }

         if (i >= 1) {
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
         }

         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         matrix.pop();
      }
   }
}
