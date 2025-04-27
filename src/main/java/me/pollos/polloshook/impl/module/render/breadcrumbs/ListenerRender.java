package me.pollos.polloshook.impl.module.render.breadcrumbs;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.movement.MovementUtil;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.api.util.color.ColorUtil;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TimedVec3d;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TraceVectors;
import me.pollos.polloshook.impl.module.render.breadcrumbs.util.TrackedVertex;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class ListenerRender extends ModuleListener<BreadCrumbs, RenderEvent> {
   public ListenerRender(BreadCrumbs module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (MovementUtil.isUpdatingPos()) {
         ((BreadCrumbs)this.module).selfTracked.add(new TimedVec3d(mc.player.getPos(), System.currentTimeMillis()));
      }

      MatrixStack matrixStack = event.getMatrixStack();
      matrixStack.push();
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      Tessellator tessellator = Tessellator.getInstance();
      RenderSystem.lineWidth((Float)((BreadCrumbs)this.module).lineWidth.getValue());
      RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
      RenderSystem.defaultBlendFunc();
      BufferBuilder bufferBuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.LINES);
      AtomicInteger buffers = new AtomicInteger();
      if (!((BreadCrumbs)this.module).selfTracked.isEmpty() && (Boolean)((BreadCrumbs)this.module).self.getValue()) {
         Vec3d prevVec = null;

         Vec3d vec;
         for(Iterator var9 = ((BreadCrumbs)this.module).selfTracked.iterator(); var9.hasNext(); prevVec = vec) {
            TimedVec3d timed = (TimedVec3d)var9.next();
            vec = timed.vec();
            Long time = timed.time();
            float alphaFactor = 5.0F * (Float)((BreadCrumbs)this.module).alphaFactor.getValue() / 10.0F;
            float factored = Math.max(0.1F, (Float)((BreadCrumbs)this.module).timeout.getValue() - alphaFactor);
            int alpha = (int)ColorUtil.fade((double)time, (double)(factored * 1000.0F));
            Color color = (Boolean)((BreadCrumbs)this.module).fade.getValue() ? ColorUtil.changeAlpha(((BreadCrumbs)this.module).color.getColor(), alpha) : ((BreadCrumbs)this.module).color.getColor();
            if (prevVec != null) {
               RenderMethods.drawLine(matrixStack, bufferBuilder, Interpolation.interpolateVec(vec), Interpolation.interpolateVec(prevVec), color);
               buffers.getAndIncrement();
            }
         }
      }

      if (!((BreadCrumbs)this.module).thrownEntities.isEmpty()) {
         Iterator var19 = ((BreadCrumbs)this.module).thrownEntities.entrySet().iterator();

         while(var19.hasNext()) {
            Entry<Integer, TrackedVertex> entry = (Entry)var19.next();
            Integer id = (Integer)entry.getKey();
            TrackedVertex thrownEntity = (TrackedVertex)entry.getValue();
            Iterator var23 = thrownEntity.vertices().iterator();

            while(var23.hasNext()) {
               TraceVectors vertex = (TraceVectors)var23.next();
               HashMap<Integer, Integer> alphas = new HashMap();
               alphas.put(id, (int)ColorUtil.fade((double)vertex.time(), (double)((Float)((BreadCrumbs)this.module).timeout.getValue() * 1000.0F)));
               Vec3d vec = Interpolation.interpolateVec(vertex.vec());
               Vec3d vec2 = Interpolation.interpolateVec(vertex.vec2());
               float alpha = 255.0F;
               if (alphas.get(id) != null) {
                  alpha = (float)(Integer)alphas.get(id);
               }

               Color color = ((TrackedVertex)entry.getValue()).friend() ? Colours.get().getFriendColor() : ((BreadCrumbs)this.module).color.getColor();
               RenderMethods.drawLine(matrixStack, bufferBuilder, (float)vec2.x, (float)vec2.y, (float)vec2.z, (float)vec.x, (float)vec.y, (float)vec.z, ColorUtil.changeAlpha(color, (int)alpha));
               buffers.getAndIncrement();
            }
         }
      }

      if (buffers.get() >= 1) {
         BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
      }

      MSAAFramebuffer.end(smoothBuffer, framebuffer);
      RenderMethods.disable3D();
      matrixStack.pop();
   }
}
