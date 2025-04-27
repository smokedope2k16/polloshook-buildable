package me.pollos.polloshook.impl.module.render.skeleton;

import me.pollos.polloshook.api.event.listener.ModuleListener;
import me.pollos.polloshook.api.minecraft.render.MSAAFramebuffer;
import me.pollos.polloshook.api.minecraft.render.RenderMethods;
import me.pollos.polloshook.impl.events.render.RenderEvent;
import net.minecraft.client.gl.Framebuffer;

public class ListenerRender extends ModuleListener<Skeleton, RenderEvent> {
   public ListenerRender(Skeleton module) {
      super(module, RenderEvent.class, -30000);
   }

   public void call(RenderEvent event) {
      RenderMethods.enable3D();
      MSAAFramebuffer smoothBuffer = MSAAFramebuffer.getInstance(4);
      Framebuffer framebuffer = mc.getFramebuffer();
      MSAAFramebuffer.start(smoothBuffer, framebuffer);
      ((Skeleton)this.module).onRender(event);
      MSAAFramebuffer.end(smoothBuffer, framebuffer);
      RenderMethods.disable3D();
   }
}
