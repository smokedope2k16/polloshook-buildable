package me.pollos.polloshook.impl.module.player.airplace;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.Interpolation;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import me.pollos.polloshook.impl.module.other.colours.Colours;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public class ListenerRender extends ModuleListener<AirPlace, RenderEvent> {
   public ListenerRender(AirPlace module) {
      super(module, RenderEvent.class);
   }

   public void call(RenderEvent event) {
      if (((AirPlace)this.module).pos != null && ((AirPlace)this.module).timer.passed((double)((Float)((AirPlace)this.module).delay.getValue() * 100.0F))) {
         Box box = Interpolation.interpolatePos(((AirPlace)this.module).pos);
         MatrixStack matrix = event.getMatrixStack();
         matrix.push();
         RenderMethods.enable3D();
         MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
         Framebuffer framebuffer = mc.getFramebuffer();
         MSAAFramebuffer.start(smoothBuffer, framebuffer);
         RenderMethods.drawBox(matrix, box, Colours.get().getColourCustomAlpha(30));
         RenderMethods.drawOutlineBox(matrix, box, Colours.get().getColor(), 1.4F);
         MSAAFramebuffer.end(smoothBuffer, framebuffer);
         RenderMethods.disable3D();
         matrix.pop();
      }
   }
}
