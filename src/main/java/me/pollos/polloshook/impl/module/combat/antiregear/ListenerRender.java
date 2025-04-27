package me.pollos.polloshook.impl.module.combat.antiregear;

import java.awt.Color;
import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class ListenerRender extends ModuleListener<AntiRegear, RenderEvent> {
   public ListenerRender(AntiRegear module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (((AntiRegear)this.module).renderPos != null) {
         MatrixStack matrix = event.getMatrixStack();
         matrix.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         Box bb = Interpolation.interpolatePos(((AntiRegear)this.module).renderPos);
         RenderMethods.drawBox(matrix, bb, new Color(255, 0, 0, 30));
         RenderMethods.drawOutlineBox(matrix, bb, new Color(255, 0, 0, 100), 1.4F);
         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         matrix.pop();
      }

   }
}
